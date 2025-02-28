package GameObjects.History;

import GameObjects.TeamsAndPlayers.Team;
import java.io.Serializable;

public class TeamSeasonRecordHistory implements Serializable {
    private final Team team;
    private final int wins;
    private final int losses;
    private final int OVR;
    private final String seasonName;

    public TeamSeasonRecordHistory(Team team, int wins, int losses, int OVR, String seasonName) {
        this.team = team;
        this.wins = wins;
        this.losses = losses;
        this.OVR = OVR;
        this.seasonName = seasonName;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public String toString() {
        String ret = "";
        ret += team.getTeamName() + " (" + OVR + ") " + wins + "-" + losses;
        return ret;
    }
}
