package GameObjects.Game;

import GameObjects.Game.GameUI.GameControllerUI;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import java.util.Map;
import java.util.HashMap;

public class GameRunner {

    public static void main(String[] args) throws InterruptedException{
        System.out.println("Game is starting");

        Map<Integer, Team> teamIDtoTeamMap = loadTestTeams();
        Map<Integer, Player> playerIDtoPlayerMap = loadTestPlayers(teamIDtoTeamMap);

        //implement logic for loading game from save 

        //this following line depends on team id 0 existing
        Game game = new Game(teamIDtoTeamMap, playerIDtoPlayerMap, teamIDtoTeamMap.get(0));
        GameControllerUI gameControllerUI = new GameControllerUI();
        UIGameService ui = new UIGameService(game, gameControllerUI);
        ui.setupActionListeners();
        game.selectTeam(teamIDtoTeamMap.get(0));

        game.loadGameWithSeasonsConfig();
        game.playGame();
    }

    static Map<Integer, Team> loadTestTeams() {
        Map<Integer, Team> teamMap = new HashMap<>();

        Team team1 = new Team(0, "TSM");
        Team team2 = new Team(1, "CLG");
        Team team3 = new Team(2, "T1");
        Team team4 = new Team(3, "TL");
        Team team5 = new Team(4, "BLG");
        Team team6 = new Team(5, "RNG");
        Team team7 = new Team(6, "EDG");
        Team team8= new Team(7, "G2");
        Team team9 = new Team(8, "FNC");
        Team team10 = new Team(9, "C9");
        teamMap.put(0, team1);
        teamMap.put(1, team2);
        teamMap.put(2, team3);
        teamMap.put(3, team4);
        teamMap.put(4, team5);
        teamMap.put(5, team6);
        teamMap.put(6, team7);
        teamMap.put(7, team8);
        teamMap.put(8, team9);
        teamMap.put(9, team10);

        return teamMap;
    }

    static Map<Integer, Team> loadTestTeams100() {
        Map<Integer, Team> teamMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            Team team = new Team(i, "team" + i);
            teamMap.put(i, team);
        }
        return teamMap;
    }

    static Map<Integer, Player> loadTestPlayers(Map<Integer, Team> teamMap) {
        Map<Integer, Player> playerIDtoPlayerMap = new HashMap<>();
        for (Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
            for (int playerIndex = 0; playerIndex < 5; playerIndex++) {
                int teamID = entry.getKey();
                Team team = entry.getValue();
                Player player = Player.generatePlayerWithTeam(teamID);
                team.addPlayer(player);
                playerIDtoPlayerMap.put(player.getPlayerID(), player);
            }
            entry.getValue().normalizePlayers();
        }
        return playerIDtoPlayerMap;
    }
}
