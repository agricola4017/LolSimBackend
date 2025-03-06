package GameObjects.MatchesAndSeasons;

import GameObjects.HerosAndClasses.Hero;
import GameObjects.HerosAndClasses.HeroEnum;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;
import GameObjects.TeamsAndPlayers.Stat;
import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import Game.GameUI.GameUIGenerator;
import Game.GameUI.TeamfightWindow;

import static Functions.Functions.rollPercentile;
import GameObjects.HerosAndClasses.HeroFactory;
import Game.GameUI.TeamfightWindow;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Match extends MatchAbstract {
    private final Team[] teams;
    private MatchLog matchLog;
    private Map<Player, Hero> playerToHeroMap;
    private List<Hero> team1heroes;
    private List<Hero> team2heroes;
    private Map<Position, Player> team1playersMap;
    private Map<Position, Player> team2playersMap;
    private Map<Player, MatchLogPlayerStat> team1PlayerToStatMap;
    private Map<Player, MatchLogPlayerStat> team2PlayerToStatMap;

    public Match(Team team1, Team team2) {
        this.teams = new Team[2];
        this.teams[0] = team1;
        this.teams[1] = team2;
        this.playerToHeroMap = new HashMap<>();
    }

    private void draftHeros() {
        team1heroes = new ArrayList<>();
        team2heroes = new ArrayList<>();
        for (Player player : teams[0].getPlayerRoster().getActivePlayers().values()) {
            Hero hero = HeroFactory.createHero();
            playerToHeroMap.put(player, hero);
            team1heroes.add(hero);
        }

        for (Player player : teams[1].getPlayerRoster().getActivePlayers().values()) {
            Hero hero = HeroFactory.createHero();
            playerToHeroMap.put(player, hero);
            team2heroes.add(hero);
        }
    }

    private int waveValue(int economy) {
        int waveGold = 280;
        int probabilityGold = 50;
        int skilledWaveGold = waveGold - probabilityGold;
        double flatWaveValue = 0.75;
        double divider = 100 / (1-flatWaveValue);
        int res =  (int)Math.round(skilledWaveGold * ((economy/divider) + flatWaveValue));
        res = res + (int)Math.round(Math.random() * probabilityGold);

        return res;
    }

    private int calculateCs(int waveValue) {
        return (int)Math.round((waveValue/280.0)*8);
    }

    private boolean kill(int lan1, int lan2, int agg1, int cons2) {
        double lanediff = .5*(lan1 - lan2);
        final double varInfl = 0.4;
        final double percentageKill = 8;
        double divider = 100/varInfl;
        double aggroConsDiff =  (agg1/divider + 0.8)/(cons2/divider + 0.8);
        double statPercentile = aggroConsDiff*lanediff;

        return rollPercentile(statPercentile) || rollPercentile(percentageKill);
    }

    public void playLanePhase() {
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
    }

    public MatchLog determineVictorAndLogStats() {
        
        Team team1 = this.teams[0];
        Team team2 = this.teams[1];
        
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

        MatchLogTeamStat team1Stat = new MatchLogTeamStat(team1, team1Kills, team1Deaths, team1Cs, team1Gold, team1PlayerToStatMap, team1PositionToPlayerMap);
        MatchLogTeamStat team2Stat = new MatchLogTeamStat(team2, team2Kills, team2Deaths, team2Cs, team2Gold, team2PlayerToStatMap, team2PositionToPlayerMap);

        //System.out.println(team1Stat);
        //System.out.println(team2Stat);
        Map<Team, MatchLogTeamStat> teamToTeamStatMap = new HashMap<>();

        teamToTeamStatMap.put(team1, team1Stat);
        teamToTeamStatMap.put(team2, team2Stat);

        //player season stat management
        for (Map.Entry<Player, MatchLogPlayerStat> entry : team1PlayerToStatMap.entrySet()) {
            Player player = entry.getKey();
            MatchLogPlayerStat playerStat = entry.getValue();
            player.updateSeasonStats(playerStat.getKills(), playerStat.getDeaths(), playerStat.getCs(), playerStat.getGold());
        }

        for (Map.Entry<Player, MatchLogPlayerStat> entry : team2PlayerToStatMap.entrySet()) {
            Player player = entry.getKey();
            MatchLogPlayerStat playerStat = entry.getValue();
            player.updateSeasonStats(playerStat.getKills(), playerStat.getDeaths(), playerStat.getCs(), playerStat.getGold());
        }

        MatchLogStats matchLogStats = new MatchLogStats(teamToTeamStatMap);
        return new MatchLog(winner, loser, matchLogStats);
    }

    public void playTeamfights() {
        for (int i = 0; i < 3; i++) {
            FightSimulation fs = new FightSimulation(teams, team1heroes, team2heroes);
            fs.generateMatchHeroes();
            fs.simulateRound();
        }
    }

    public void playSimulatedTeamfights() {
        GameUIGenerator gameUIGen = new GameUIGenerator();
        for (int i = 0; i < 1; i++) {
            CountDownLatch latch = new CountDownLatch(1);

            SwingUtilities.invokeLater(() -> {
                FightSimulation fs = new FightSimulation(teams, team1heroes, team2heroes);
                TeamfightWindow tfw = new TeamfightWindow(fs, team1heroes, team2heroes, latch);
                tfw.setVisible(true);

                tfw.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        latch.countDown();
                        gameUIGen.createOrUpdateTextPanel(
                            "Fight Statistics", "Fight Statistics", 
                            fs.getMatchStatistics());
                    }
                });
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void initializeStatsAndMaps() {
        Team team1 = this.teams[0];
        Team team2 = this.teams[1];

        team1playersMap = team1.getPlayerRoster().getActivePlayers();
        team2playersMap = team2.getPlayerRoster().getActivePlayers();

        //System.out.println(team1.getPlayerRoster().getActivePlayers());
        team1PlayerToStatMap = new HashMap<>();
        team2PlayerToStatMap = new HashMap<>();

        for (Player p: team1.getPlayerRoster().getRosterAsList()) {
            team1PlayerToStatMap.put(p,new MatchLogPlayerStat(p, 0, 0, 0,0, playerToHeroMap.get(p).getHeroEnum()));
        }

        for (Player p: team2.getPlayerRoster().getRosterAsList()) {
            team2PlayerToStatMap.put(p,new MatchLogPlayerStat(p, 0, 0, 0, 0, playerToHeroMap.get(p).getHeroEnum()));
        }
    }
    public void playMatch() {
        draftHeros();
        initializeStatsAndMaps();
        //lane phase
        playLanePhase();
        //teamfights + macro, gold
        playTeamfights();
        //determine victory through end score
        matchLog = determineVictorAndLogStats();
        HeroEnum.resetAvailableHeroes();
    }

    public void playSimulatedMatch() {
        draftHeros();
        initializeStatsAndMaps();
        //lane phase
        playLanePhase();
        //teamfights + macro, gold
        playSimulatedTeamfights();
        //determine victory through end score
        matchLog = determineVictorAndLogStats();
        HeroEnum.resetAvailableHeroes();
    }

    public Team[] getTeams() {
        return teams;
    }

    public MatchLog getMatchLog() {
        return matchLog;
    }

    public Team getWinner() {
        return matchLog.getWinner();
    }

    public Team getLoser() {
        return matchLog.getLoser();
    }

    @Override
    public String toString() {
        if (matchLog == null) {
            throw new RuntimeException("Match not played yet");
        }
        return teams[0].getTeamName() + ", " + teams[0].getPlayerRoster().getOVR() + " vs " + teams[1].getTeamName() + ", " + teams[1].getPlayerRoster().getOVR() + ", winner is " + matchLog.getWinner().getTeamName();
    }


/*    public String toString() {
        return teams[0] +" vs " +teams[1];
    }*/

}
