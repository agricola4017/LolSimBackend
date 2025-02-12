package GameObjects.TeamsAndPlayers;

import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

public class Standing implements Comparable<Standing>, Serializable {

    private int wins;
    private int losses;
    private Team team;

    private List<Boolean> last5;
        

    public Standing(Team team) {
        this.wins = 0;
        this.losses = 0;
        this.team = team;

        this.last5 = new LinkedList<Boolean>();
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
        if (this.last5.size() == 5) {
            this.last5.remove(0);   
        }
        this.last5.add(true);
        this.wins++;
    }

    public void lostGame() {
        if (this.last5.size() == 5) {
            this.last5.remove(0);
        }   
        this.last5.add(false);
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
        // Logging the state of the team and its players
        /**00 
        if (team == null) {
            System.out.println("Standing: team is null");
        } else {
            System.out.println("Standing: team name = " + team.getTeamName());
            if (team.getPlayerRoster() == null) {
                System.out.println("Standing: playerRoster is null");
            } else {
                System.out.println("Standing: playerRoster is initialized");
            }
        }
        **/
        int last5Wins = 0;
        for (Boolean b : last5) {
            if (b) {
                last5Wins++;
            }
        }
        return team.getTeamName() + ": " + wins + "-" + (losses) + " | " + (wins + losses)
            + " | " + last5Wins + "-" + (last5.size() - last5Wins);
    }
}
