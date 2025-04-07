package GameObjects.HerosAndClasses;

public class HeroStatsTrackers {
    private int wins;
    private int losses;

    public HeroStatsTrackers(int wins, int losses) {
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

    public double getWinrate() {
        if (wins + losses == 0) {
            return 0.5;
        }
        return (double) wins / (wins + losses);
    }
}
