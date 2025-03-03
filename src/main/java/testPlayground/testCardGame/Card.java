package testPlayground.testCardGame;

public class Card {
    private String name;
    private int energyCost;
    private CardType type;
    private String description;
    private int value; // Could represent damage for attack cards, block amount for defend cards, etc.

    public enum CardType {
        ATTACK, DEFEND, UTILITY
    }

    public Card(String name, int energyCost, CardType type, String description, int value) {
        this.name = name;
        this.energyCost = energyCost;
        this.type = type;
        this.description = description;
        this.value = value;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public CardType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + " (" + energyCost + " Energy) - " + type + " - " + description;
    }
}