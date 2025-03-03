package testPlayground.testCardGame;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    private static final int MAX_HAND_SIZE = 5;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public boolean addCard(Card card) {
        if (cards.size() < MAX_HAND_SIZE) {
            cards.add(card);
            return true;
        }
        return false; // Hand is full
    }

    public Card playCard(int index, int availableEnergy) {
        if (index < 0 || index >= cards.size()) {
            return null; // Invalid index
        }
        
        Card card = cards.get(index);
        if (card.getEnergyCost() > availableEnergy) {
            return null; // Not enough energy
        }
        
        return cards.remove(index);
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards); // Return a copy to prevent direct manipulation
    }

    public int getSize() {
        return cards.size();
    }
}