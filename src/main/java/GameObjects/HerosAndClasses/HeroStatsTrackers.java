package testPlayground.testingDynamicPatching;

public class StatsTrackers {
    private int wins;
    private int losses;

    public StatsTrackers(int wins, int losses) {
        this.wins = wins;
        this.losses = losses;
    }

    public int getWins() {
        return wins;
    }   

    public int getLosses() {
        return losses;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    public void resetStats() {
        wins = 0;
        losses = 0;
    }

}
