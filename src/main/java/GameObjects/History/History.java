package GameObjects.Game.History;

import java.util.Map;

import GameObjects.TeamsAndPlayers.Standing;
import GameObjects.TeamsAndPlayers.Team;
import GameObjects.Game.MatchesAndSeasons.Season;
import GameObjects.Game.MatchesAndSeasons.SpringSplit;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Some basic implementation of historical stats and records 
 */
public class History implements Serializable{
        /**
     * Stat objects
     * 
     * not incl. in load/save yet
     */

     private Map<Integer, TeamSeasonRecordHistory> winningTeams;
     private Map<Integer, TeamSeasonRecordHistory> runnerUpTeams;
     
     private int startYear;
     private int currSplitCount;

     public History(int startYear) {
         this.startYear = startYear;
         this.currSplitCount = startYear -1;
         this.winningTeams = new HashMap<>();
         this.runnerUpTeams = new HashMap<>();
     }

     public void recordSeason(Season season) {
         this.currSplitCount++;
         Standing winnerStanding = season.getStanding(season.getWinner());
         Standing runnerUpStanding = season.getStanding(season.getRunnerUp());
         Team winner = season.getWinner();
         Team runnerUp = season.getRunnerUp();
         TeamSeasonRecordHistory winnerRecord = new TeamSeasonRecordHistory(winner, winnerStanding.getWins(), winnerStanding.getLosses(), winner.getPlayerRoster().getOVR(), season.getName());
         TeamSeasonRecordHistory runnerUpRecord = new TeamSeasonRecordHistory(runnerUp, runnerUpStanding.getWins(), runnerUpStanding.getLosses(), runnerUp.getPlayerRoster().getOVR(), season.getName());
         this.winningTeams.put(currSplitCount, winnerRecord);
         this.runnerUpTeams.put(currSplitCount, runnerUpRecord);
     }

     public String getLeagueHistory() {
         String ret = "SEASON | YEAR | WINNER | RUNNER UP\n";
         int j = this.startYear -1;
         for (int i = this.startYear; i < this.currSplitCount; i++) {
             if ((i - this.startYear) % 2 == 0) {
                 j++;
             }
             ret += this.winningTeams.get(i).getSeasonName() + " | " + j + " | " + this.winningTeams.get(i).toString() + " | " + this.runnerUpTeams.get(i).toString() + "\n";
         }
         return ret;
     }
}
