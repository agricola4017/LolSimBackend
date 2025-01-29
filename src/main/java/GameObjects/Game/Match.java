package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;
import GameObjects.TeamsAndPlayers.Stat;
import GameObjects.TeamsAndPlayers.Team;

import java.util.HashMap;
import java.util.Map;

import static Functions.Functions.rollPercentile;

class Match extends MatchAbstract {
    private Team[] teams;
    private int matches;
    private MatchLog matchLog;
    Match(Team team1, Team team2, int matches) {
        this.teams = new Team[2];
        this.teams[0] = team1;
        this.teams[1] = team2;
        this.matches = matches;
    }

    int waveValue(int economy) {
        int waveGold = 280;
        int probabilityGold = 50;
        int skilledWaveGold = waveGold - probabilityGold;
        double flatWaveValue = 0.75;
        double divider = 100 / (1-flatWaveValue);
        int res =  (int)Math.round(skilledWaveGold * ((economy/divider) + flatWaveValue));
        res = res + (int)Math.round(Math.random() * probabilityGold);

        return res;
    }

    int calculateCs(int waveValue) {
        return (int)Math.round((waveValue/280.0)*8);
    }

    boolean kill(int lan1, int lan2, int agg1, int cons2) {
        double lanediff = .6*(lan1 - lan2);
        final double varInfl = 0.4;
        final double percentageKill = 8;
        double divider = 100/varInfl;
        double aggroConsDiff =  (agg1/divider + 0.8)/(cons2/divider + 0.8);
        double statPercentile = aggroConsDiff*lanediff;

        return rollPercentile(statPercentile) || rollPercentile(percentageKill);
    }
    void playMatch() {
        Team team1 = this.teams[0];
        Team team2 = this.teams[1];

        Map<Position, Player> team1playersMap = team1.getPlayerRoster().getActivePlayers();
        Map<Position, Player> team2playersMap = team2.getPlayerRoster().getActivePlayers();

        //System.out.println(team1.getPlayerRoster().getActivePlayers());
        Map<Player, MatchLogPlayerStat> team1PlayerToStatMap = new HashMap<>();
        Map<Player, MatchLogPlayerStat> team2PlayerToStatMap = new HashMap<>();

        for (Player p: team1.getPlayerRoster().getRosterAsList()) {
            team1PlayerToStatMap.put(p,new MatchLogPlayerStat(0, 0, 0, 0));
        }

        for (Player p: team2.getPlayerRoster().getRosterAsList()) {
            team2PlayerToStatMap.put(p,new MatchLogPlayerStat(0, 0, 0, 0));
        }

        //lane phase

        for (int i = 0; i < 20; i++) {
            for (Position position : Position.getPositions()) {
                Player player1 = team1playersMap.get(position);
                Player player2 = team2playersMap.get(position);

                Stat player1Stat = player1.getStat();
                Stat player2Stat = player2.getStat();

                int lane1 = player1Stat.getLaning();
                int lane2 = player2Stat.getLaning();
                int agg1 = player1Stat.getAggression();
                int agg2 = player2Stat.getAggression();
                int cons1 = player1Stat.getConsistency();
                int cons2 = player2Stat.getConsistency();
                int econ1 = player1Stat.getEconomy();
                int econ2 = player2Stat.getEconomy();

                int p1WaveValue = waveValue(econ1);
                int p2WaveValue = waveValue(econ2);

                team1PlayerToStatMap.get(player1).addGold(p1WaveValue);
                team1PlayerToStatMap.get(player1).addCs(calculateCs(p1WaveValue));
                team2PlayerToStatMap.get(player2).addGold(p2WaveValue);
                team2PlayerToStatMap.get(player2).addCs(calculateCs(p2WaveValue));

                final int killValue = 300;

                if (kill(lane1, lane2, agg1, cons2)) {
                    team1PlayerToStatMap.get((player1)).addKill(1);
                    team2PlayerToStatMap.get((player2)).addDeath(1);
                    team1PlayerToStatMap.get(player1).addGold(killValue);
                }

                if (kill(lane2, lane1, agg2, cons1)) {
                    team2PlayerToStatMap.get((player2)).addKill(1);
                    team1PlayerToStatMap.get((player1)).addDeath(1);
                    team2PlayerToStatMap.get(player2).addGold(killValue);
                }
            }
        }

        //teamfights + macro, gold

        //determine victory through end score

        int team1Gold = 0;
        int team1Kills = 0;
        int team1Deaths = 0;
        int team1Cs = 0;
        int team2Gold = 0;
        int team2Kills = 0;
        int team2Deaths = 0;
        int team2Cs = 0;
        for (Position position : Position.getPositions()) {
            Player player1 = team1playersMap.get(position);
            Player player2 = team2playersMap.get(position);

            team1Cs += team1PlayerToStatMap.get(player1).getCs();
            team2Cs += team2PlayerToStatMap.get(player2).getCs();

            team1Kills += team1PlayerToStatMap.get(player1).getKills();
            team2Kills += team2PlayerToStatMap.get(player2).getKills();

            team1Deaths += team1PlayerToStatMap.get(player1).getDeaths();
            team2Deaths += team2PlayerToStatMap.get(player2).getDeaths();

            team1Gold += team1PlayerToStatMap.get(player1).getGold();
            team2Gold += team2PlayerToStatMap.get(player2).getGold();
        }

        Team winner;
        Team loser;
        //System.out.println(team1Gold + " " + team2Gold + " " + (50 + 100*(team1Gold - team2Gold)/team2Gold));
        if (rollPercentile(50 + 100*(team1Gold - team2Gold)/team2Gold)) {
            winner = team1;
            loser = team2;
        } else {
            winner = team2;
            loser = team1;
        }


        Map<Position, Player> team1PositionToPlayerMap = team1.getPlayerRoster().getActivePlayers();
        Map<Position, Player> team2PositionToPlayerMap = team2.getPlayerRoster().getActivePlayers();

        MatchLogTeamStat team1Stat = new MatchLogTeamStat(team1Kills, team1Deaths, team1Cs, team1Gold, team1PlayerToStatMap, team1PositionToPlayerMap);
        MatchLogTeamStat team2Stat = new MatchLogTeamStat(team2Kills, team2Deaths, team2Cs, team2Gold, team2PlayerToStatMap, team2PositionToPlayerMap);

        //System.out.println(team1Stat);
        //System.out.println(team2Stat);
        Map<Team, MatchLogTeamStat> teamToTeamStatMap = new HashMap<>();

        teamToTeamStatMap.put(team1, team1Stat);
        teamToTeamStatMap.put(team2, team2Stat);

        MatchLogStats matchLogStats = new MatchLogStats(teamToTeamStatMap);
        matchLog = new MatchLog(winner, loser, matchLogStats);
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public MatchLog getMatchLog() {
        return matchLog;
    }

    public void setMatchLog(MatchLog matchLog) {
        this.matchLog = matchLog;
    }

    @Override
    public String toString() {
        return teams[0].getTeamName() + ", " + teams[0].getPlayerRoster().getOVR() + " vs " + teams[1].getTeamName() + ", " + teams[1].getPlayerRoster().getOVR() + " for " + matches + " matches, winner is " + matchLog.getWinner().getTeamName();
    }


/*    public String toString() {
        return teams[0] +" vs " +teams[1];
    }*/

}
