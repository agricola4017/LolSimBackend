package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Team;

abstract class MatchAbstract {
    private int winner;
    private int loser;
    private int[] teams;
    private int matches;

    abstract void playMatch();

    abstract int getWinner();

    abstract int getLoser();

}
