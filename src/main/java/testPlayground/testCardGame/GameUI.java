package testPlayground.testCardGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameUI extends JFrame {
    private Player player;
    private Player opponent;
    private JPanel playerHandPanel;
    private JPanel gameLogPanel;
    private JTextArea gameLog;
    private JLabel playerInfo;
    private JLabel opponentInfo;
    private JButton endTurnButton;
    private JPanel playAreaPanel;
    private boolean isPlayerTurn;
    private List<CardPanel> cardPanels;
    
    public GameUI(Player player, Player opponent) {
        this.player = player;
        this.opponent = opponent;
        this.isPlayerTurn = true;
        this.cardPanels = new ArrayList<>();
        
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Opponent area (top)
        JPanel opponentPanel = new JPanel(new BorderLayout());
        opponentInfo = new JLabel("Opponent: Health " + opponent.getHealth() + ", Energy " + opponent.getEnergy());
        opponentInfo.setFont(new Font("Arial", Font.BOLD, 16));
        opponentInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        opponentPanel.add(opponentInfo, BorderLayout.NORTH);
        
        // Opponent's hand would be hidden - just show card backs
        JPanel opponentHandPanel = new JPanel(new FlowLayout());
        for (int i = 0; i < opponent.getHand().getSize(); i++) {
            JPanel cardBack = new JPanel();
            cardBack.setPreferredSize(new Dimension(100, 150));
            cardBack.setBackground(new Color(100, 50, 50));
            cardBack.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            opponentHandPanel.add(cardBack);
        }
        opponentPanel.add(opponentHandPanel, BorderLayout.CENTER);
        add(opponentPanel, BorderLayout.NORTH);
        
        // Play area (middle)
        playAreaPanel = new JPanel(new BorderLayout());
        playAreaPanel.setBorder(BorderFactory.createTitledBorder("Play Area"));
        
        // Game log section
        gameLogPanel = new JPanel(new BorderLayout());
        gameLogPanel.setBorder(BorderFactory.createTitledBorder("Game Log"));
        gameLog = new JTextArea(10, 40);
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);
        gameLogPanel.add(scrollPane, BorderLayout.CENTER);
        playAreaPanel.add(gameLogPanel, BorderLayout.EAST);
        
        // Card play visualization area
        JPanel cardPlayVisualization = new JPanel();
        cardPlayVisualization.setPreferredSize(new Dimension(400, 200));
        cardPlayVisualization.setBorder(BorderFactory.createEtchedBorder());
        playAreaPanel.add(cardPlayVisualization, BorderLayout.CENTER);
        
        add(playAreaPanel, BorderLayout.CENTER);
        
        // Player area (bottom)
        JPanel playerPanel = new JPanel(new BorderLayout());
        
        // Player info
        playerInfo = new JLabel("Player: Health " + player.getHealth() + ", Energy " + player.getEnergy());
        playerInfo.setFont(new Font("Arial", Font.BOLD, 16));
        playerInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        playerPanel.add(playerInfo, BorderLayout.NORTH);
        
        // Player hand
        playerHandPanel = new JPanel(new FlowLayout());
        playerPanel.add(playerHandPanel, BorderLayout.CENTER);
        
        // End turn button
        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endPlayerTurn();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(endTurnButton);
        playerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(playerPanel, BorderLayout.SOUTH);
        
        // Start game
        initializeGame();
    }
    
    private void initializeGame() {
        // Initialize hands
        player.drawInitialHand();
        opponent.drawInitialHand();
        
        // Start with player's turn
        startPlayerTurn();
        
        // Initial log message
        logMessage("Game started. It's your turn!");
    }
    
    private void startPlayerTurn() {
        isPlayerTurn = true;
        player.startTurn();
        player.drawCard();
        updatePlayerHand();
        updateInfoLabels();
        endTurnButton.setEnabled(true);
        
        logMessage("--- Your turn ---");
        logMessage("You drew a card. You have " + player.getEnergy() + " energy.");
    }
    
    private void endPlayerTurn() {
        isPlayerTurn = false;
        endTurnButton.setEnabled(false);
        
        // Schedule opponent's turn to happen after a short delay
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playOpponentTurn();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void playOpponentTurn() {
        logMessage("--- Opponent's turn ---");
        opponent.startTurn();
        opponent.drawCard();
        updateInfoLabels();
        
        // Simple AI for opponent - play first affordable card
        boolean playedCard = false;
        for (int i = 0; i < opponent.getHand().getSize(); i++) {
            Card card = opponent.getHand().getCards().get(i);
            
            if (card.getEnergyCost() <= opponent.getEnergy()) {
                // Visual feedback of card being played
                visualizeCardPlayed(card, false);
                final int index = i;
                
                // Slight delay before applying effect
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean success = opponent.playCard(index, player);
                        if (success) {
                            updateInfoLabels();
                            logMessage("Opponent played " + card.getName());
                            
                            // Check if game is over
                            if (player.getHealth() <= 0) {
                                gameOver(false);
                                return;
                            }
                        }
                        
                        // End opponent turn after a delay
                        Timer endTurnTimer = new Timer(1000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                startPlayerTurn();
                            }
                        });
                        endTurnTimer.setRepeats(false);
                        endTurnTimer.start();
                    }
                });
                timer.setRepeats(false);
                timer.start();
                
                playedCard = true;
                break;
            }
        }
        
        if (!playedCard) {
            logMessage("Opponent could not play any cards!");
            
            // End opponent turn after a delay
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startPlayerTurn();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void updatePlayerHand() {
        playerHandPanel.removeAll();
        cardPanels.clear();
        
        for (Card card : player.getHand().getCards()) {
            CardPanel cardPanel = new CardPanel(card);
            
            // Can only play if it's player's turn and they have enough energy
            boolean playable = isPlayerTurn && card.getEnergyCost() <= player.getEnergy();
            cardPanel.setPlayable(playable);
            
            cardPanel.addPlayListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playCard(cardPanel);
                }
            });
            
            playerHandPanel.add(cardPanel);
            cardPanels.add(cardPanel);
        }
        
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }
    
    private void playCard(CardPanel cardPanel) {
        Card card = cardPanel.getCard();
        int cardIndex = player.getHand().getCards().indexOf(card);
        
        if (cardIndex >= 0) {
            // Visual feedback first
            visualizeCardPlayed(card, true);
            
            // Apply effect after a delay
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean success = player.playCard(cardIndex, opponent);
                    
                    if (success) {
                        logMessage("You played " + card.getName());
                        updatePlayerHand();
                        updateInfoLabels();
                        
                        // Check if game over
                        if (opponent.getHealth() <= 0) {
                            gameOver(true);
                        }
                    } else {
                        logMessage("Failed to play " + card.getName());
                    }
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void visualizeCardPlayed(Card card, boolean byPlayer) {
        // Display the card being played in the play area
        JPanel playVisual = new JPanel(new BorderLayout());
        playVisual.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        JLabel playerLabel = new JLabel(byPlayer ? "Player plays:" : "Opponent plays:");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        CardPanel displayCard = new CardPanel(card);
        displayCard.setPlayable(false); // Just for display
        
        playVisual.add(playerLabel, BorderLayout.NORTH);
        playVisual.add(displayCard, BorderLayout.CENTER);
        
        // Replace any existing content in the play visualization area
        JPanel cardPlayArea = (JPanel) playAreaPanel.getComponent(1);
        cardPlayArea.removeAll();
        cardPlayArea.setLayout(new GridBagLayout()); // Center the card
        cardPlayArea.add(playVisual);
        cardPlayArea.revalidate();
        cardPlayArea.repaint();
    }
    
    private void updateInfoLabels() {
        playerInfo.setText("Player: Health " + player.getHealth() + ", Energy " + player.getEnergy());
        opponentInfo.setText("Opponent: Health " + opponent.getHealth() + ", Energy " + opponent.getEnergy());
        
        // Update card playability based on current energy
        for (CardPanel panel : cardPanels) {
            boolean playable = isPlayerTurn && panel.getCard().getEnergyCost() <= player.getEnergy();
            panel.setPlayable(playable);
        }
    }
    
    private void logMessage(String message) {
        gameLog.append(message + "\n");
        // Scroll to the bottom
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }
    
    private void gameOver(boolean playerWon) {
        String message = playerWon ? "Congratulations! You won!" : "Game over! You lost!";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        
        // Disable further plays
        isPlayerTurn = false;
        endTurnButton.setEnabled(false);
        updatePlayerHand();
        
        logMessage("--- GAME OVER ---");
        logMessage(message);
    }
}