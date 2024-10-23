package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Team;

public class Standing implements Comparable<Standing> {

    private int wins;
    private int losses;
    private Team team;

    public Standing(Team team) {
        this.wins = 0;
        this.losses = 0;
        this.team = team;
    }

    @Override
    public int compareTo(Standing b) {
        if (this.wins == b.getWins()) {
            if (this.losses == b.getLosses()) {
                return 0;
            } else {
                return this.getLosses() - b.getLosses();
            }
        } else {
            return b.getWins() - this.getWins();
        }
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void wonGame() {
        this.wins++;
    }

    public void lostGame() {
        this.losses++;
    }
    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return team.getTeamName() + ": " + wins + "-" + (losses) + " | " +(wins+losses);
    }
}
