package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Team;

abstract class MatchAbstract {
    private Team[] teams;
    private int matches;
    private MatchLog matchLog;
    abstract void playMatch();


}
