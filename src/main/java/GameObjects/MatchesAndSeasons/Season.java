package GameObjects.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;
import GameObjects.TeamsAndPlayers.Standing;

import java.io.Serializable;
import java.util.List;
import java.util.Deque;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.stream.Collectors;

import GameObjects.HerosAndClasses.HeroFactory;

/**
 * Season Class
 * 
 * Defines a season of a League 
 * * * for now references Spring Split and Spring Playoffs, may be redefined to inclue non-playable seasons like off-season 
 * 
 * A season is defined by a list of matches to be played, a list of teams, a list of standings, and a winner
 */
public abstract class Season implements Serializable {
    /**
     * recalculate value 
     * move free agents to cache, generate players and push to db
     * expired contracts
     * 30D signing period (value decreases, AI for bots to sign teams 
     * recalculate OVR/POT
     */
    // spring
    /**
     * bo1 swiss format
     * 
     */
    
    // spring playoffs
    /**
     * top 6 advance, create bracket
     * bo3
     * no loser's bracket
     */
    // summer 

    /**
     * copy spring for now, implement bo3 later
     */
    //summer playoffs
    /**
     * top 6 advance, create bracket
     * bo5
     * with losers bracket later, copy spring p for now
     */
    // worlds (implement later)

    /**
     * Stats season
     * calculate stats, mvp, HoF
     */

    private Deque<Series> seriesToBePlayed;

    //how many matches played in one set (one hit of playAll)
    private final int oneSetCount;
    private final String name;
    private final List<Standing> standings;
    private final List<Standing> oldStandings;
    private final List<Team> teams;
    private final Map<Integer, Standing> teamIDToStandingMap;
    
    private Team winner;
    private Team runnerUp;

    protected Season(List<Team> teams, String name) {
        this.teams = teams;
        this.oneSetCount = teams.size()/2;
        this.seriesToBePlayed = new ArrayDeque<>();
        this.standings = new ArrayList<>();
        this.teamIDToStandingMap = new HashMap<>();
        this.oldStandings = new ArrayList<>();
        this.name = name;
        this.winner = null;
        this.runnerUp = null;

        initStandings();
    }

    protected Season(List<Team> teams, List<Standing> oldStandings, String name) {
        this.teams = teams;
        this.oneSetCount = teams.size()/2;
        this.seriesToBePlayed = new ArrayDeque<>();
        this.standings = new ArrayList<>();
        this.teamIDToStandingMap = new HashMap<>();
        this.oldStandings = oldStandings;
        this.name = name;
        this.winner = null;
        this.runnerUp = null;

        initStandings();
    }

    /**
     * Plays a match
     * Assumes there are matches to be played
     * @return Match the match that was played
     */
    public Match playMatch() {
        Series series = seriesToBePlayed.peek();
        Match match = series.playMatch();
        updateStandings(match.getWinner(), match.getLoser());
        if (series.isFinished()) {
            seriesToBePlayed.poll();
        }
        return match;
    }

    public Match playSimulatedMatch() {
        Series series = seriesToBePlayed.peek();
        Match match = series.playSimulatedMatch();
        updateStandings(match.getWinner(), match.getLoser());
        if (series.isFinished()) {
            seriesToBePlayed.poll();
        }
        return match;
    }
    public boolean isTeamInNextSeries(Team team) {
        return seriesToBePlayed.peek().isTeamInSeries(team);
    }

    protected void updateStandings(Team winner, Team loser) {
        teamIDToStandingMap.get(winner.getTeamID()).wonGame();
        teamIDToStandingMap.get(loser.getTeamID()).lostGame();
        Collections.sort(this.standings);
    }

    public abstract boolean isFinished();

    public String getName() {
        return this.name;
    }

    public Team getWinner() {
        return this.winner;
    }

    public Team getRunnerUp() {
        return this.runnerUp;
    }

    protected Series peekSeriesToBePlayed() {
        return seriesToBePlayed.peek();
    }

    protected Series pollSeriesToBePlayed() {
        return seriesToBePlayed.poll();
    }

    public int getNumberOfTeams() {
        return teams.size();
    }

    public Boolean containsTeam(Team team) {
        return teams.contains(team);
    }

    /**
     * Sets the winner of the season if it has not already been set
     * @param winner
     */
    protected void setWinner(Team winner) {
        if (this.winner != null) {
            return;
        }
        this.winner = winner;
    }   

    protected void setRunnerUp(Team runnerUp) {
        if (this.runnerUp != null) {
            return;
        }
        this.runnerUp = runnerUp;
    }   

    protected int getOneSetCount() {
        return oneSetCount;
    }
    
    /**
     * Returns a copy of the list of teams in the season
     * @return List<Team> list of teams
     */
    protected List<Team> getTeams() {
        return new ArrayList<>(this.teams);
    }
    
    protected List<Standing> getStandings() {
        return this.standings;
    }
    
    public Standing getStanding(Team team) {
        return this.teamIDToStandingMap.get(team.getTeamID());
    }

    protected List<Standing> getOldStandings() {
        return this.oldStandings;
    }

    protected void addSeries(Series series) {
        this.seriesToBePlayed.add(series);  
    }

    protected Boolean isSeriesToBePlayedEmptyAndNotNull() {
        return seriesToBePlayed != null && seriesToBePlayed.isEmpty();
    }

    public abstract Season generateNextSeason(List<Team> teams);

    /**
     * Initializes the standings based on the teams in the league
     */
    private void initStandings() {
        for (int i = 0; i < this.teams.size(); i++) {
            Standing standing = new Standing(teams.get(i));
            this.standings.add(standing);
            this.teamIDToStandingMap.put(teams.get(i).getTeamID(), standing);
        }
    }

    /**
     * Sorts the standings, records the placements 
     * @param splitCount
     */
    public void postSeasonCleanup(int splitCount) {
        Collections.sort(standings);

        standings.get(0).getTeam().addWin();
        for (int i = 0; i < standings.size(); i++) {
            Standing s = standings.get(i);
            s.getTeam().addPlacement(i + 1, splitCount);
        }
        HeroFactory.resetStatsTrackers();
    }

    public List<Team> repopulateTeamInOrderOfStandings() {
        Collections.sort(standings);

        return standings.stream().map(Standing::getTeam).collect(Collectors.toList());
    }

    public String[] getStandingsColumnNames() {
        return new String[]{"Position", "Team", "Record", "Last 5", "OVR", "Previous Record"};
    }

    public String[][] getStandingsData() {
        String[][] data = new String[standings.size()][getStandingsColumnNames().length];
        
        // Add standings data
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            String positionAndTeam = (i + 1) + ". " + standing.getTeam().getTeamName();
            String record = standing.getWins() + "-" + standing.getLosses();
            String last5 = standing.getLast5Wins() + "-" + standing.getLast5Losses();
            String ovr = String.valueOf(standing.getTeam().getPlayerRoster().getOVR());
            String prevRecord = oldStandings != null && !oldStandings.isEmpty() && i < oldStandings.size() 
                ? oldStandings.get(i).getWins() + "-" + oldStandings.get(i).getLosses() 
                : "0-0";
            
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = standing.getTeam().getTeamName();
            data[i][2] = record;
            data[i][3] = last5;
            data[i][4] = ovr;
            data[i][5] = prevRecord;
        }
        
        return data;
    }

    public String getStatusText() {
        return isFinished() ? getName() + " Winner: " + getWinner().getTeamName() : getName() + " is in progress";
    }

    public String toString() {
        String ret = "";
        
        int j = 1;
        for (int i = 0; i < this.standings.size(); i++) {
            Standing standing = this.standings.get(i);
            String standingOutput = j + ". " + standing + " | (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " | Prev. ";
            if (this.oldStandings != null && !this.oldStandings.isEmpty()) {
                standingOutput += this.oldStandings.get(i);
            } else {
                standingOutput += "0-0";
            }
            ret += standingOutput + " | TID:" + standing.getTeam().getTeamID() + "\n";
            j++;
        }
        return ret;
    }
}
