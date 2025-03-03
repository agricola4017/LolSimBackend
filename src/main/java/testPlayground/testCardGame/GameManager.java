package testPlayground.testCardGame;

import java.util.Scanner;

public class GameManager {
    private Player player;
    private Player opponent;
    private Scanner scanner;
    private boolean isPlayerTurn;

    public GameManager(Player player, Player opponent) {
        this.player = player;
        this.opponent = opponent;
        this.scanner = new Scanner(System.in);
        this.isPlayerTurn = true; // Player goes first
    }

    public void startGame() {
        // Shuffle decks
        player.drawInitialHand();
        opponent.drawInitialHand();
        
        // Main game loop
        while (player.getHealth() > 0 && opponent.getHealth() > 0) {
            if (isPlayerTurn) {
                playerTurn();
            } else {
                opponentTurn();
            }
            isPlayerTurn = !isPlayerTurn; // Switch turns
        }
        
        // Game over
        if (player.getHealth() <= 0) {
            System.out.println("Game Over! " + opponent.getName() + " wins!");
        } else {
            System.out.println("Game Over! " + player.getName() + " wins!");
        }
    }

    private void playerTurn() {
        player.startTurn();
        player.drawCard();
        
        System.out.println("\n--- " + player.getName() + "'s Turn ---");
        System.out.println("Health: " + player.getHealth());
        System.out.println("Energy: " + player.getEnergy());
        
        displayHand();
        
        boolean endTurn = false;
        while (!endTurn) {
            System.out.println("Choose a card to play (1-" + player.getHand().getSize() + ") or 0 to end turn:");
            int choice = scanner.nextInt();
            
            if (choice == 0) {
                endTurn = true;
            } else if (choice > 0 && choice <= player.getHand().getSize()) {
                boolean success = player.playCard(choice - 1, opponent);
                if (!success) {
                    System.out.println("Not enough energy or invalid card selection!");
                }
                displayHand();
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    private void opponentTurn() {
        opponent.startTurn();
        opponent.drawCard();
        
        System.out.println("\n--- " + opponent.getName() + "'s Turn ---");
        
        // Simple AI for opponent - just play the first card that is affordable
        boolean playedCard = false;
        for (int i = 0; i < opponent.getHand().getSize(); i++) {
            if (opponent.getHand().getCards().get(i).getEnergyCost() <= opponent.getEnergy()) {
                playedCard = true;
                break;
            }
        }
        
        if (!playedCard) {
            System.out.println(opponent.getName() + " could not play any cards!");
        }
    }

    private void displayHand() {
        System.out.println("\nYour hand:");
        int index = 1;
        for (Card card : player.getHand().getCards()) {
            System.out.println(index + ". " + card);
            index++;
        }
    }
}
