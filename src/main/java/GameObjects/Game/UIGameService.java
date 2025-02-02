package GameObjects.Game;

import GameObjects.Game.GameUI.GameControllerUI;
import GameObjects.Game.GameUI.GameUIGenerator;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;
import java.util.concurrent.CountDownLatch;
import java.util.List;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import Functions.Functions;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javax.swing.JButton;

public class UIGameService {

    private Game game;
    private Map<JButton, ActionListener> buttonToActionListenerMap = new HashMap<>();
    private JButton playSeasonButton;
    private JButton playGameButton;
    private JButton seeStandingsButton;
    private JButton seeTeamInfoButton;
    private JButton signPlayerButton;
    private JButton playTeamGameButton;
    private JButton findPlayerButton;
    private JButton seePlayersButton;
    private JButton findTeamButton;
    private JButton saveGameButton;
    private JButton loadGameButton;

    public UIGameService(Game game, GameControllerUI gameControllerUI) {
        this.playSeasonButton = gameControllerUI.getPlaySeasonButton();
        this.playGameButton = gameControllerUI.getPlayGameButton();
        this.seeStandingsButton = gameControllerUI.getSeeStandingsButton();
        this.seeTeamInfoButton = gameControllerUI.getSeeTeamInfoButton();
        this.signPlayerButton = gameControllerUI.getSignPlayerButton();
        this.playTeamGameButton = gameControllerUI.getPlayTeamGameButton();
        this.findPlayerButton = gameControllerUI.getFindPlayerButton();
        this.seePlayersButton = gameControllerUI.getSeePlayersButton();
        this.findTeamButton = gameControllerUI.getFindTeamButton();
        this.saveGameButton = gameControllerUI.getSaveGameButton();
        this.loadGameButton = gameControllerUI.getLoadGameButton();
        this.game = game;
    }

    /**
     * this should likely be its own class
     * @param gameControllerUI
     * @param currentSeason
     */ 
    void setupActionListeners() {

        ActionListener playSeasonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Season currentSeason = game.getSeasonsToPlay().peek();
                MatchLog matchLog = game.playSeason(currentSeason);
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();
                Team playingTeam = game.getPlayingTeam();
                
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", game.standingsToString());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);         
                if (matchLog != null)
                    GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                try {
                    Thread.sleep(100);
                    game.countDownLatch();
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

            }
        };

        buttonToActionListenerMap.put(playSeasonButton, playSeasonListener);
        playSeasonButton.addActionListener(playSeasonListener);

        ActionListener playGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {   
                Season currentSeason = game.getSeasonsToPlay().peek();   
                MatchLog matchLog = game.playMatch(currentSeason);
                Team playingTeam = game.getPlayingTeam();
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();

                if (matchLog != null) {
                    GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                    // Also update standings after each game
                    GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", game.standingsToString());
                    String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                    GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                if (currentSeason.isFinished()) {
                    game.countDownLatch();
                }
            }
        };

        
        playGameButton.addActionListener(playGameListener);
        buttonToActionListenerMap.put(playGameButton, playGameListener);

        ActionListener playTeamGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Season currentSeason = game.getSeasonsToPlay().peek();
                MatchLog matchLog;
                Team playingTeam = game.getPlayingTeam();
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();

                do {
                    matchLog = game.playMatch(currentSeason);
                } while (matchLog != null && (matchLog.getWinner() != playingTeam && matchLog.getLoser() != playingTeam));
                
                if (matchLog != null) {
                GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                }    
                // Also update standings after each game
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", game.standingsToString());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                if (currentSeason.isFinished()) {
                    game.countDownLatch();
                }
            }           
        };

        buttonToActionListenerMap.put(playTeamGameButton, playTeamGameListener);
        playTeamGameButton.addActionListener(playTeamGameListener);
        

        ActionListener seeStandingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", game.standingsToString());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        };
        seeStandingsButton.addActionListener(seeStandingsListener);
        buttonToActionListenerMap.put(seeStandingsButton, seeStandingsListener);

        ActionListener seeTeamInfoListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Team playingTeam = game.getPlayingTeam();
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();

                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + 
                    playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        };
        seeTeamInfoButton.addActionListener(seeTeamInfoListener);
        buttonToActionListenerMap.put(seeTeamInfoButton, seeTeamInfoListener);

        ActionListener signPlayerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get player signing form
                Team playingTeam = game.getPlayingTeam();
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();
                List<Player> activePlayers = game.getActivePlayers();
                Map<Integer, Player> playerIDtoPlayerMap = game.getPlayerIDtoPlayerMap();
                GameUIGenerator.createPlayerSigningForm((name, position, ovr) -> {
                    Player player = Player.generateNamedPlayerFromOVRandPosition(
                        name, 
                        ovr, 
                        playingTeam.getTeamID(), 
                        position
                    );
                    playerIDtoPlayerMap.put(player.getPlayerID(), player);
                    activePlayers.add(player);
                    playingTeam.addPlayer(player);
                    playingTeam.normalizePlayers();
                    String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                    GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                });
            }
        };
        signPlayerButton.addActionListener(signPlayerListener);
        buttonToActionListenerMap.put(signPlayerButton, signPlayerListener);

        ActionListener findPlayerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get player search form
                Map<Integer, Player> playerIDtoPlayerMap = game.getPlayerIDtoPlayerMap();

                GameUIGenerator.createFindByIDForm("Find Player by ID","Find Player by ID", (playerID) -> {
                    if (!playerIDtoPlayerMap.containsKey(playerID)) {
                        GameUIGenerator.updateIDForm("Find Player by ID", "Player not found");
                        return;
                    }
                    Player player = playerIDtoPlayerMap.get(playerID);
                    GameUIGenerator.updateIDForm("Find Player by ID", player.toString() + "\n" + player.getStat().toString());
                });
                
            }
        };
        findPlayerButton.addActionListener(findPlayerListener);
        buttonToActionListenerMap.put(findPlayerButton, findPlayerListener);

        ActionListener seePlayersListener = new ActionListener() {
            List<Player> activePlayers = game.getActivePlayers();
            @Override
            public void actionPerformed(ActionEvent e) {
                GameUIGenerator.createOrUpdateTextFrame("players", "Players", Functions.flattenListString(activePlayers));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        };
        seePlayersButton.addActionListener(seePlayersListener);
        buttonToActionListenerMap.put(seePlayersButton, seePlayersListener);

        ActionListener findTeamListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Integer, Team> teamIDtoTeamMap = game.getTeamIDtoTeamMap();
                // Get player search form
                GameUIGenerator.createFindByIDForm("Find Team by ID","Find Team by ID", (teamID) -> {
                    if (!teamIDtoTeamMap.containsKey(teamID)) {
                        GameUIGenerator.updateIDForm("Find Team by ID", "Team not found");
                        return;
                    }
                    Team team = teamIDtoTeamMap.get(teamID);
                    GameUIGenerator.updateIDForm("Find Team by ID", team.toString());
                });
                
            }
        };
        findTeamButton.addActionListener(findTeamListener);
        buttonToActionListenerMap.put(findTeamButton, findTeamListener);

        ActionListener saveGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save the game
                try (FileOutputStream fos = new FileOutputStream("game.save");
                ObjectOutputStream oos = new ObjectOutputStream(fos);) {
                    oos.writeObject(game.getGameIsRunning());
                    oos.writeObject(game.getSeasonsToPlay());
                    oos.writeObject(game.getTeams());
                    oos.writeObject(game.getActivePlayers());
                    oos.writeObject(game.getTeamIDtoTeamMap());
                    oos.writeObject(game.getPlayerIDtoPlayerMap());
                    oos.writeObject(game.getStandings());
                    oos.writeObject(game.getOldStandings());
                    oos.writeObject(game.getTeamToStandingMap());
                    oos.writeObject(game.getPlayingTeam());
                } catch (IOException ex) {
                    System.out.println("Error saving game :" + ex.getMessage());
                }
            }
        };

        saveGameButton.addActionListener(saveGameListener);
        buttonToActionListenerMap.put(saveGameButton, saveGameListener);

        ActionListener loadGameListener = new ActionListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void actionPerformed(ActionEvent e) {
                // Load the game
                try (FileInputStream fis = new FileInputStream("game.save");
                     ObjectInputStream ois = new ObjectInputStream(fis);) {
                    boolean gameIsRunning = (boolean) ois.readObject();
                    Queue<Season> seasonsToPlay = (Queue<Season>) ois.readObject();
                    List<Team> teams = (List<Team>) ois.readObject();
                    List<Player> activePlayers = (List<Player>) ois.readObject();
                    Map<Integer, Team> teamIDtoTeamMap = (Map<Integer, Team>) ois.readObject();
                    Map<Integer, Player> playerIDtoPlayerMap = (Map<Integer, Player>) ois.readObject();
                    List<Standing> standings = (List<Standing>) ois.readObject();
                    List<Standing> oldStandings = (List<Standing>) ois.readObject();
                    Map<Team, Standing> teamToStandingMap = (Map<Team, Standing>) ois.readObject();
                    Team playingTeam = (Team) ois.readObject();
                    game.loadGame(gameIsRunning, seasonsToPlay, teams, activePlayers, teamIDtoTeamMap, playerIDtoPlayerMap, standings, oldStandings, teamToStandingMap, playingTeam);
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Error loading game: " + ex.getMessage());
                } 
                    // Also update standings after each game
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", game.standingsToString());
                Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();
                Team playingTeam = game.getPlayingTeam();
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
            }
        };
        loadGameButton.addActionListener(loadGameListener);
        buttonToActionListenerMap.put(loadGameButton, loadGameListener);
    }
}