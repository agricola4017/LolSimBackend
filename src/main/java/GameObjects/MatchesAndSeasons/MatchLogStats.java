package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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
        List<MatchLogTeamStat> values = new ArrayList<>(matchLogTeamStatMap.values());
        String ret = "Match Log Stats: \n";
        ret +="Kills: " + values.get(0).getKills() + " - " + values.get(1).getKills() + "\n";
        ret += "Deaths: " + values.get(0).getDeaths() + " - " + values.get(1).getDeaths() + "\n";
        ret += "CS: " + values.get(0).getCs() + " - " + values.get(1).getCs() + "\n";
        ret += "Gold: " + values.get(0).getGold() + " - " + values.get(1).getGold() + "\n";
        ret += "------------------------\n";
        ret += values.get(0).toString() + "\n";
        ret += values.get(1).toString();
        return ret;
    }
}

