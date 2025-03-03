package testPlayground.testCardGame;

public class CustomGameManager {
    private GameUI gameUI;
    
    public CustomGameManager(Player player, Player opponent) {
        gameUI = new GameUI(player, opponent);
    }
    
    public void startGame() {
        gameUI.setVisible(true);
    }
}

