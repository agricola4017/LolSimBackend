package GameObjects.Game.MatchesAndSeasons;

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
    private final Map<Team, Standing> teamToStandingMap;
    
    private Team winner;

    protected Season(List<Team> teams) {
        this.teams = teams;
        this.oneSetCount = teams.size()/2;
        this.seriesToBePlayed = new ArrayDeque<>();
        this.standings = new ArrayList<>();
        this.teamToStandingMap = new HashMap<>();
        this.oldStandings = new ArrayList<>();
        this.name = "Season";
        this.winner = null;

        initStandings();
    }

    protected Season(List<Team> teams, List<Standing> oldStandings) {
        this.teams = teams;
        this.oneSetCount = teams.size()/2;
        this.seriesToBePlayed = new ArrayDeque<>();
        this.standings = new ArrayList<>();
        this.teamToStandingMap = new HashMap<>();
        this.oldStandings = oldStandings;
        this.name = "Season";
        this.winner = null;

        initStandings();
    }

    /**
     * Plays a match
     * Assumes there are matches to be played
     * @return Match the match that was played
     */
    public Match playMatch() {
        Series series = seriesToBePlayed.peek();
        if (series.isFinished()) {
            seriesToBePlayed.poll();
        }
        series = seriesToBePlayed.peek();
        Match match = series.pollMatch();
        for (Team team : match.getTeams()) {
            team.normalizePlayers();
        }
        match.playMatch();
        teamToStandingMap.get(match.getWinner()).wonGame();
        teamToStandingMap.get(match.getLoser()).lostGame();
        Collections.sort(this.standings);
        return match;
    }

    public abstract boolean isFinished();

    public String getName() {
        return this.name;
    }

    public Team getWinner() {
        return this.winner;
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
        return this.teamToStandingMap.get(team);
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
    public void initStandings() {
        for (int i = 0; i < this.teams.size(); i++) {
            Standing standing = new Standing(teams.get(i));
            this.standings.add(standing);
            this.teamToStandingMap.put(teams.get(i), standing);
        }
    }

    public void postSeasonCleanup(int splitCount) {
        Collections.sort(standings);

        standings.get(0).getTeam().addWin();
        for (int i = 0; i < standings.size(); i++) {
            Standing s = standings.get(i);
            s.getTeam().addPlacement(i + 1, splitCount);
        }
    }

    public List<Team> repopulateTeamInOrderOfStandings() {
        Collections.sort(standings);

        return standings.stream().map(Standing::getTeam).collect(Collectors.toList());
    }

    public String toString() {
        String ret = "";
        
        int j = 1;
        for (int i = 0; i < this.standings.size(); i++) {
            Standing standing = this.standings.get(i);
            String standingOutput = j + ". " + standing + " | (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " | Prev. ";
            if (this.oldStandings != null) {
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
