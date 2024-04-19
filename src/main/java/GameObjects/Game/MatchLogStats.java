package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import java.util.Map;

public class MatchLogStats {
    private Map<Team, MatchLogTeamStat> matchLogTeamStatMap;
    private int goldDiff;
    private int killDiff;

    public MatchLogStats(Map<Team, MatchLogTeamStat> matchLogTeamStatMap) {
        this.matchLogTeamStatMap = matchLogTeamStatMap;
    }

    public Map<Team, MatchLogTeamStat> getMatchLogTeamStatMap() {
        return matchLogTeamStatMap;
    }

    public void setMatchLogPlayerStatMap(Map<Team, MatchLogTeamStat> matchLogTeamStatMap) {
        this.matchLogTeamStatMap = matchLogTeamStatMap;
    }

    @Override
    public String toString() {
        return "MatchLogStats{" +
                "matchLogTeamStatMap=" + matchLogTeamStatMap +
                '}';
    }
}

