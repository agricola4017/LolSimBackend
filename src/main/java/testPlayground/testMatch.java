package testPlayground;

import GameObjects.Game.MatchesAndSeasons.Match;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import GameObjects.Game.MatchesAndSeasons.Season;
import GameObjects.Game.MatchesAndSeasons.SpringPlayoffs;
import java.util.stream.Collectors;

import java.util.*;

public class testMatch {
    static List<Team> teams = new ArrayList<>();
    static Map<Integer, Team> teamMap = new HashMap<>();
    static List<Player> activePlayers = new ArrayList<>();
    public static void main(String[] args) {
        testTeams();
        testPlayers();

        teams.get(0).getPlayerRoster().normalizePlayers(teams.get(0).getPlayers());
        teams.get(1).getPlayerRoster().normalizePlayers(teams.get(1).getPlayers());
        teams.get(2).getPlayerRoster().normalizePlayers(teams.get(2).getPlayers());
        teams.get(3).getPlayerRoster().normalizePlayers(teams.get(3).getPlayers());
        teams.get(4).getPlayerRoster().normalizePlayers(teams.get(4).getPlayers());
        teams.get(5).getPlayerRoster().normalizePlayers(teams.get(5).getPlayers());
        teams.get(6).getPlayerRoster().normalizePlayers(teams.get(6).getPlayers());
        teams.get(7).getPlayerRoster().normalizePlayers(teams.get(7).getPlayers());

        //4 teams 1 v 4, 2v3 -> 1v2, 3v4 -> 2v3 -> 1v2 
        testSeries();
     }

    static void testSeries() {
        Season season = new SpringPlayoffs(teams.subList(0, 6));
        List<String> teamNames = teams.subList(0,6).stream().map(Team::getTeamName).collect(Collectors.toList());
        System.out.println("Out of the teams: " + teamNames);
        while (!season.isFinished()) {
            season.playMatch();
        }

        System.out.println("Winner of Playoffs is: " + season.getWinner().getTeamName());
    }
    static void testMatches() {
        int t1win = 0;
        int t2win = 0;
        for (int i = 0; i < 1000; i++) {
            Match a = new Match(teams.get(0), teams.get(1));
            a.playMatch();
            if (a.getMatchLog().getWinner() == teams.get(0)) {
                t1win++;
            } else {
                t2win++;
            }
        }
        System.out.println(teams.get(0).getPlayerRoster().getOVR() + " " + teams.get(1).getPlayerRoster().getOVR());
        System.out.println(teams.get(0).getTeamName() + " wins: " + t1win + " " + teams.get(1).getTeamName() + " wins: " + t2win);
    }
    static void testPlayers() {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < 5; j++) {
                Player player = Player.generatePlayerWithTeam(i);
                activePlayers.add(player);
                teamMap.get(i).addPlayer(player);
            }
        }
    }

    static void testPlayers2() {
        int i = 0;
        for (int j = 0; j < 5; j++) {
            Player player = Player.generatePerfectPlayer(i);
            activePlayers.add(player);
            teamMap.get(i).addPlayer(player);
        }

        i = 1;
        for (int j = 0; j < 5; j++) {
            Player player = Player.generatePlayerWithTeam(i);
            activePlayers.add(player);
            teamMap.get(i).addPlayer(player);
        }
    }

    static void testTeams() {

        Team team1 = new Team(0, "TSM");
        Team team2 = new Team(1, "CLG");
        Team team3 = new Team(2, "G2");
        Team team4 = new Team(3, "FNC");    
        Team team5 = new Team(4, "OG");
        Team team6 = new Team(5, "100");
        Team team7 = new Team(6, "J4");
        Team team8 = new Team(7, "IMT");

        teamMap.put(0, team1);
        teamMap.put(1, team2);
        teamMap.put(2, team3);
        teamMap.put(3, team4);
        teamMap.put(4, team5);
        teamMap.put(5, team6);
        teamMap.put(6, team7);
        teamMap.put(7, team8);

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        teams.add(team7);
        teams.add(team8);

    }
}


