package GameObjects.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

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

    @Override
    public String toString() {
        return winner.getTeamName() + "(W) - " +  "OVR: " + winner.getPlayerRoster().getOVR() + 
                "\n" + loser.getTeamName() + "(L) - " + "OVR: " + loser.getPlayerRoster().getOVR() +
                "\n" + matchLogStats;
    }
}
