package GameObjects.MatchesAndSeasons;

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

    public String[] getSummaryColumnNames() {
        List<MatchLogTeamStat> values = new ArrayList<>(matchLogTeamStatMap.values());
        return new String[]{"Stat", values.get(0).getTeam().getTeamName(), values.get(1).getTeam().getTeamName()};
    }

    public String[][] getSummaryData() {
        List<MatchLogTeamStat> values = new ArrayList<>(matchLogTeamStatMap.values());
        String[][] data = new String[6][3];
        
        // Add Winner/Loser status
        data[0][0] = "Status";
        data[0][1] = "Winner";
        data[0][2] = "Loser";
        
        // Add OVR
        data[1][0] = "OVR";
        data[1][1] = String.valueOf(values.get(0).getTeam().getPlayerRoster().getOVR());
        data[1][2] = String.valueOf(values.get(1).getTeam().getPlayerRoster().getOVR());
        
        // Add match stats
        data[2][0] = "Kills";
        data[2][1] = String.valueOf(values.get(0).getKills());
        data[2][2] = String.valueOf(values.get(1).getKills());
        
        data[3][0] = "Deaths";
        data[3][1] = String.valueOf(values.get(0).getDeaths());
        data[3][2] = String.valueOf(values.get(1).getDeaths());
        
        data[4][0] = "CS";
        data[4][1] = String.valueOf(values.get(0).getCs());
        data[4][2] = String.valueOf(values.get(1).getCs());
        
        data[5][0] = "Gold";
        data[5][1] = String.valueOf(values.get(0).getGold());
        data[5][2] = String.valueOf(values.get(1).getGold());
        
        return data;
    }

    public static String[] getTeamStatsColumnNames() {
        return MatchLogTeamStat.getColumnNames();
    }

    public String[][] getTeamStatsData(Team team) {
        MatchLogTeamStat teamStat = matchLogTeamStatMap.get(team);
        return teamStat != null ? teamStat.getData() : new String[0][6];
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

