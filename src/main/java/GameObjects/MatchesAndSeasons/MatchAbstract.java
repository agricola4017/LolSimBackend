package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;
import java.io.Serializable;

abstract class MatchAbstract implements Serializable {
    private Team[] teams;
    private int matches;
    private MatchLog matchLog;
    abstract void playMatch();


}
