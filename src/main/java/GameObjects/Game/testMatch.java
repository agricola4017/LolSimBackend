package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

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

        int t1win = 0;
        int t2win = 0;
        for (int i = 0; i < 1000; i++) {
            Match a = new Match(teams.get(0), teams.get(1), 1);
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

        teamMap.put(0, team1);
        teamMap.put(1, team2);

        teams.add(team1);
        teams.add(team2);

    }
}


