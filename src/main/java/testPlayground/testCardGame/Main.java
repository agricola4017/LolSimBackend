package testPlayground.testCardGame;

// Updated Main.java to use the GUI version
public class Main {
    public static void main(String[] args) {
        // Create a sample deck for the player
        Deck playerDeck = new Deck();
        
        // Add some sample cards
        playerDeck.addCard(new Card("Slash", 1, Card.CardType.ATTACK, "Deal 6 damage", 6));
        playerDeck.addCard(new Card("Heavy Strike", 2, Card.CardType.ATTACK, "Deal 10 damage", 10));
        playerDeck.addCard(new Card("Block", 1, Card.CardType.DEFEND, "Gain 5 block", 5));
        playerDeck.addCard(new Card("Shield Wall", 2, Card.CardType.DEFEND, "Gain 8 block", 8));
        playerDeck.addCard(new Card("Draw Card", 0, Card.CardType.UTILITY, "Draw a card", 1));
        playerDeck.addCard(new Card("Energy Potion", 0, Card.CardType.UTILITY, "Gain 1 energy", 1));
        
        // Add more cards to have a decent deck size
        playerDeck.addCard(new Card("Quick Strike", 1, Card.CardType.ATTACK, "Deal 4 damage", 4));
        playerDeck.addCard(new Card("Bash", 2, Card.CardType.ATTACK, "Deal 8 damage", 8));
        playerDeck.addCard(new Card("Parry", 1, Card.CardType.DEFEND, "Gain 4 block", 4));
        playerDeck.addCard(new Card("Dodge", 1, Card.CardType.DEFEND, "Gain 6 block", 6));
        
        playerDeck.shuffle();
        
        // Create a sample deck for the opponent
        Deck opponentDeck = new Deck();
        
        // Add some sample cards for opponent
        opponentDeck.addCard(new Card("Enemy Strike", 1, Card.CardType.ATTACK, "Deal 5 damage", 5));
        opponentDeck.addCard(new Card("Enemy Block", 1, Card.CardType.DEFEND, "Gain 5 block", 5));
        opponentDeck.addCard(new Card("Power Up", 1, Card.CardType.UTILITY, "Gain strength", 1));
        
        // Add more cards for opponent's deck
        opponentDeck.addCard(new Card("Enemy Bash", 2, Card.CardType.ATTACK, "Deal 8 damage", 8));
        opponentDeck.addCard(new Card("Enemy Shield", 2, Card.CardType.DEFEND, "Gain 8 block", 8));
        opponentDeck.addCard(new Card("Enemy Strike", 1, Card.CardType.ATTACK, "Deal 5 damage", 5));
        opponentDeck.addCard(new Card("Enemy Block", 1, Card.CardType.DEFEND, "Gain 5 block", 5));
        opponentDeck.addCard(new Card("Power Up", 1, Card.CardType.UTILITY, "Gain strength", 1));
        opponentDeck.addCard(new Card("Enemy Double Strike", 3, Card.CardType.ATTACK, "Deal 12 damage", 12));
        opponentDeck.addCard(new Card("Enemy Wall", 3, Card.CardType.DEFEND, "Gain 12 block", 12));
        
        opponentDeck.shuffle();
        
        // Create players
        Player player = new Player("Player", playerDeck, 3, 50);
        Player opponent = new Player("Enemy", opponentDeck, 3, 40);
        
        // Create and start game with GUI
        CustomGameManager game = new CustomGameManager(player, opponent);
        game.startGame();
    }
}