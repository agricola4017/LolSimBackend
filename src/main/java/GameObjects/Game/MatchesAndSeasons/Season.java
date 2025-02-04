package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

import java.io.Serializable;
import java.util.List;
import java.util.Deque;

public abstract class Season implements Serializable{ 
    
    // free agent
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

    private Deque<Match> matchesToBePlayed;

    //how many matches played in one set
    private int oneSetCount;
    private String name;
    private Team winner;
    
    public Match playMatch() {
        Match match = matchesToBePlayed.poll();
        match.playMatch();
        return match;
    }
    public abstract int getOneSetCount();

    public abstract String getName();

    public abstract boolean isFinished();

    public abstract Deque<Match> getMatchesToBePlayed();

    public abstract Season newInstance(List<Team> teams);

    public abstract Team getWinner();

    public abstract Season generateNextSeason(List<Team> teams);
}
