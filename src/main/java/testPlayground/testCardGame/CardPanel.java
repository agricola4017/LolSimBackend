package testPlayground.testCardGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CardPanel extends JPanel {
    private Card card;
    private JButton playButton;
    private JLabel cardInfo;
    private JLabel energyCost;
    private JLabel cardType;

    public CardPanel(Card card) {
        this.card = card;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(200, 150));
        
        // Card title
        JLabel cardName = new JLabel(card.getName());
        cardName.setFont(new Font("Arial", Font.BOLD, 16));
        cardName.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Energy cost with icon
        energyCost = new JLabel("Energy: " + card.getEnergyCost());
        energyCost.setForeground(new Color(0, 100, 200));
        energyCost.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Card type with color
        cardType = new JLabel(card.getType().toString());
        cardType.setFont(new Font("Arial", Font.ITALIC, 14));
        
        // Set color based on card type
        Color cardColor;
        switch(card.getType()) {
            case ATTACK:
                cardColor = new Color(255, 200, 200); // Light red
                cardType.setForeground(new Color(180, 0, 0));
                break;
            case DEFEND:
                cardColor = new Color(200, 255, 200); // Light green
                cardType.setForeground(new Color(0, 150, 0));
                break;
            case UTILITY:
                cardColor = new Color(200, 200, 255); // Light blue
                cardType.setForeground(new Color(0, 0, 180));
                break;
            default:
                cardColor = Color.WHITE;
                break;
        }
        setBackground(cardColor);
        
        // Card description
        cardInfo = new JLabel("<html><body>" + card.getDescription() + "<br>Value: " + card.getValue() + "</body></html>");
        cardInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Play button
        playButton = new JButton("Play");
        
        // Layout components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(cardName, BorderLayout.CENTER);
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 1));
        statsPanel.setOpaque(false);
        statsPanel.add(energyCost);
        statsPanel.add(cardType);
        
        topPanel.add(statsPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(cardInfo, BorderLayout.CENTER);
        add(playButton, BorderLayout.SOUTH);
    }
    
    public Card getCard() {
        return card;
    }
    
    public void setPlayable(boolean playable) {
        playButton.setEnabled(playable);
    }
    
    public void addPlayListener(ActionListener listener) {
        playButton.addActionListener(listener);
    }
}