package testPlayground.testCardGame;

public class Player {
    private String name;
    private Deck deck;
    private Hand hand;
    private int energy;
    private int maxEnergy;
    private int health;

    public Player(String name, Deck deck, int maxEnergy, int health) {
        this.name = name;
        this.deck = deck;
        this.hand = new Hand();
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
        this.health = health;
    }

    public void drawInitialHand() {
        for (int i = 0; i < 5; i++) {
            if (deck.getSize() > 0) {
                Card card = deck.drawCard();
                hand.addCard(card);
            }
        }
    }

    public void startTurn() {
        // Reset energy at the start of each turn
        energy = maxEnergy;
    }

    // Placeholder methods for card effects
    private void takeDamage(int amount) {
        // Could implement block mechanic here
        health -= amount;
        if (health < 0) health = 0;
        System.out.println(name + " now has " + health + " health");
    }

    private void gainBlock(int amount) {
        // Placeholder for block mechanics
        System.out.println(name + " gained " + amount + " block");
    }

    public String getName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public int getHealth() {
        return health;
    }

    public Hand getHand() {
        return hand;
    }

    public void drawCard() {
        if (deck.getSize() > 0) {
            Card card = deck.drawCard();
            if (!hand.addCard(card)) {
                // Handle case where hand is full
                System.out.println(name + " has a full hand. Could not draw a card.");
            }
        } else {
            System.out.println(name + " has no more cards to draw!");
        }
    }

    public Card getCardAt(int index) {
        if (index < 0 || index >= hand.getSize()) {
            return null;
        }
        return hand.getCards().get(index);
    }
    
    // Modify the playCard method to return information about what happened
    public boolean playCard(int handIndex, Player target) {
        Card card = hand.playCard(handIndex, energy);
        if (card == null) {
            return false; // Either invalid index or not enough energy
        }
        
        // Reduce energy
        energy -= card.getEnergyCost();
        
        // Apply card effect based on type
        switch (card.getType()) {
            case ATTACK:
                target.takeDamage(card.getValue());
                break;
            case DEFEND:
                this.gainBlock(card.getValue());
                break;
            case UTILITY:
                // Handle utility effects
                if (card.getName().contains("Draw")) {
                    this.drawCard();
                } else if (card.getName().contains("Energy")) {
                    this.energy += card.getValue();
                }
                break;
        }
        
        return true;
    }
}