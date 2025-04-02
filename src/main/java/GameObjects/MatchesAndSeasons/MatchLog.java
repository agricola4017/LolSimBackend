package GameObjects.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;
import GameObjects.TeamsAndPlayers.Position;
import GameObjects.TeamsAndPlayers.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchLog {

    private Team winner;
    private Team loser;
    private MatchLogStats matchLogStats;

    public MatchLog(Team winner, Team loser, MatchLogStats matchLogStats) {
        this.winner = winner;
        this.loser = loser;
        this.matchLogStats = matchLogStats;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public Team getLoser() {
        return loser;
    }

    public void setLoser(Team loser) {
        this.loser = loser;
    }

    public MatchLogStats getMatchLogStats() {
        return matchLogStats;
    }

    public void setMatchLogStats(MatchLogStats matchLogStats) {
        this.matchLogStats = matchLogStats;
    }

    public static String[] getResultColumnNames() {
        return new String[]{"Team", "Result", "OVR"};
    }

    public String[][] getResultData() {
        String[][] data = new String[2][3];
        
        // Winner data
        data[0][0] = winner.getTeamName();
        data[0][1] = "W";
        data[0][2] = String.valueOf(winner.getPlayerRoster().getOVR());
        
        // Loser data
        data[1][0] = loser.getTeamName();
        data[1][1] = "L";
        data[1][2] = String.valueOf(loser.getPlayerRoster().getOVR());
        
        return data;
    }

    public String[] getSummaryColumnNames() {
        return matchLogStats.getSummaryColumnNames();
    }

    public String[][] getSummaryData() {
        return matchLogStats.getSummaryData();
    }

    public static String[] getTeamStatsColumnNames() {
        return MatchLogStats.getTeamStatsColumnNames();
    }

    public String[][] getTeamStatsData(Team team) {
        return matchLogStats.getTeamStatsData(team);
    }

    public static String[] getFullMatchLogColumnNames() {
        String[] resultCols = getResultColumnNames();
        String[] summaryCols = new String[]{"Stat", "Team 1", "Team 2"}; // Default column names
        String[] playerCols = MatchLogTeamStat.getColumnNames();
        
        // Combine all column names, removing duplicates
        String[] allCols = new String[resultCols.length + summaryCols.length + playerCols.length];
        System.arraycopy(resultCols, 0, allCols, 0, resultCols.length);
        System.arraycopy(summaryCols, 0, allCols, resultCols.length, summaryCols.length);
        System.arraycopy(playerCols, 0, allCols, resultCols.length + summaryCols.length, playerCols.length);
        
        return allCols;
    }

    public String[][] getFullMatchLogData() {
        List<MatchLogTeamStat> teamStats = new ArrayList<>(matchLogStats.getMatchLogTeamStatMap().values());
        MatchLogTeamStat winnerStats = teamStats.get(0);
        MatchLogTeamStat loserStats = teamStats.get(1);
        
        // Calculate total rows needed: 2 for result rows + 2 for summary rows + player rows for both teams
        int totalRows = 2 + 2 + winnerStats.getPositionToPlayerMap().size() + loserStats.getPositionToPlayerMap().size();
        String[][] data = new String[totalRows][13];
        int currentRow = 0;

        // Add result rows
        data[currentRow][0] = winner.getTeamName();
        data[currentRow][1] = "W";
        data[currentRow][2] = String.valueOf(winner.getPlayerRoster().getOVR());
        currentRow++;

        data[currentRow][0] = loser.getTeamName();
        data[currentRow][1] = "L";
        data[currentRow][2] = String.valueOf(loser.getPlayerRoster().getOVR());
        currentRow++;

        // Add summary rows
        data[currentRow][0] = "Kills";
        data[currentRow][3] = String.valueOf(winnerStats.getKills());
        data[currentRow][4] = String.valueOf(loserStats.getKills());
        currentRow++;

        data[currentRow][0] = "Deaths";
        data[currentRow][3] = String.valueOf(winnerStats.getDeaths());
        data[currentRow][4] = String.valueOf(loserStats.getDeaths());
        currentRow++;

        // Add winner team player stats
        for (Map.Entry<Position, Player> entry : winnerStats.getPositionToPlayerMap().entrySet()) {
            Player player = entry.getValue();
            MatchLogPlayerStat playerStat = winnerStats.getPlayerToPlayerStatMap().get(player);
            
            data[currentRow][0] = winner.getTeamName();
            data[currentRow][1] = "W";
            data[currentRow][7] = entry.getKey().toString();
            data[currentRow][8] = player.getPlayerName();
            data[currentRow][9] = playerStat.getKills() + "/" + playerStat.getDeaths();
            data[currentRow][10] = String.valueOf(playerStat.getCs());
            data[currentRow][11] = String.valueOf(playerStat.getGold());
            data[currentRow][12] = player.getStat().getOVR() + "/" + player.getStat().getPotential();
            currentRow++;
        }

        // Add loser team player stats
        for (Map.Entry<Position, Player> entry : loserStats.getPositionToPlayerMap().entrySet()) {
            Player player = entry.getValue();
            MatchLogPlayerStat playerStat = loserStats.getPlayerToPlayerStatMap().get(player);
            
            data[currentRow][0] = loser.getTeamName();
            data[currentRow][1] = "L";
            data[currentRow][7] = entry.getKey().toString();
            data[currentRow][8] = player.getPlayerName();
            data[currentRow][9] = playerStat.getKills() + "/" + playerStat.getDeaths();
            data[currentRow][10] = String.valueOf(playerStat.getCs());
            data[currentRow][11] = String.valueOf(playerStat.getGold());
            data[currentRow][12] = player.getStat().getOVR() + "/" + player.getStat().getPotential();
            currentRow++;
        }

        return data;
    }

    @Override
    public String toString() {
        return winner.getTeamName() + "(W) - " +  "OVR: " + winner.getPlayerRoster().getOVR() + 
                "\n" + loser.getTeamName() + "(L) - " + "OVR: " + loser.getPlayerRoster().getOVR() +
                "\n" + matchLogStats;
    }
}
