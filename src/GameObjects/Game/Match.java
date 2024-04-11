package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Team;

import java.util.Arrays;

import static Functions.GenerateFunctions.generateStatRangeInclusive;

class Match extends MatchAbstract {
    private int winner;
    private int loser;
    private int[] teams;
    private int matches;
    Match(int team1, int team2, int matches) {
        this.teams = new int[2];
        this.teams[0] = team1;
        this.teams[1] = team2;
        this.matches = matches;
    }
    @Override
    void playMatch() {
        int i = generateStatRangeInclusive(0,1);
        this.winner = this.teams[i];
        if (i == 1) {
            i = 0;
        } else {
            i = 1;
        }
        this.loser = this.teams[i];
    }

    int getWinner() {
        return this.winner;
    }

    int getLoser() {
        return this.loser;
    }

    @Override
    public String toString() {
        return teams[0] + " vs " + teams[1] + " for " + matches + " matches, winner is " + this.winner;
    }


/*    public String toString() {
        return teams[0] +" vs " +teams[1];
    }*/

}
