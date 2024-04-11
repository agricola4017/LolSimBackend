package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Team;

import java.util.List;
import java.util.Queue;

public abstract class Season {
    
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

    private Queue<Match> matchesToBePlayed;
    private int oneSetCount;
    private String name;
    Match playMatch() {
        Match match = matchesToBePlayed.poll();
        match.playMatch();
        return match;
    }
    abstract int getOneSetCount();

    abstract String getName();

    abstract boolean isFinished();

    abstract Queue<Match> getMatchesToBePlayed();

    abstract Season newInstance(List<Integer> teams);
}
