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

import static Functions.Functions.flattenListString;

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
    private GameUIGenerator gameUIGenerator;

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
        this.gameUIGenerator = new GameUIGenerator();
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
                
                refreshOrCreateTeamInfo();
                refreshOrCreateStandings();
                if (matchLog != null)
                    refreshOrCreateMatchLog(matchLog);
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

                if (matchLog != null) {
                    refreshOrCreateMatchLog(matchLog);
                    // Also update standings after each game
                    refreshOrCreateStandings();
                    refreshOrCreateTeamInfo();
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

                do {
                    matchLog = game.playMatch(currentSeason);
                } while (matchLog != null && (matchLog.getWinner() != playingTeam && matchLog.getLoser() != playingTeam));
                
                if (matchLog != null) {
                    refreshOrCreateMatchLog(matchLog);
                }    
                // Also update standings after each game
                refreshOrCreateStandings();
                refreshOrCreateTeamInfo();

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
                refreshOrCreateStandings();
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
                refreshOrCreateTeamInfo();
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
                List<Player> activePlayers = game.getActivePlayers();
                Map<Integer, Player> playerIDtoPlayerMap = game.getPlayerIDtoPlayerMap();
                gameUIGenerator.createPlayerSigningForm((name, position, ovr) -> {
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
                    refreshOrCreateTeamInfo();
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

                gameUIGenerator.createFindByIDForm("Find Player by ID","Find Player by ID", (playerID) -> {
                    if (!playerIDtoPlayerMap.containsKey(playerID)) {
                        gameUIGenerator.updateIDForm("Find Player by ID", "Player not found");
                        return;
                    }
                    Player player = playerIDtoPlayerMap.get(playerID);
                    gameUIGenerator.updateIDForm("Find Player by ID", player.toString() + "\n" + player.getStat().toString());
                });
                
            }
        };
        findPlayerButton.addActionListener(findPlayerListener);
        buttonToActionListenerMap.put(findPlayerButton, findPlayerListener);

        ActionListener seePlayersListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               refreshOrCreatePlayerInfo();
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
                gameUIGenerator.createFindByIDForm("Find Team by ID","Find Team by ID", (teamID) -> {
                    if (!teamIDtoTeamMap.containsKey(teamID)) {
                        gameUIGenerator.updateIDForm("Find Team by ID", "Team not found");
                        return;
                    }
                    Team team = teamIDtoTeamMap.get(teamID);
                    gameUIGenerator.updateIDForm("Find Team by ID", team.toString());
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
                refreshOrCreateStandings();
                refreshOrCreateTeamInfo();
            }
        };
        loadGameButton.addActionListener(loadGameListener);
        buttonToActionListenerMap.put(loadGameButton, loadGameListener);
    }

    void refreshOrCreateStandings() {
        gameUIGenerator.createOrUpdateTextPanel("standings", "Standings", 
            game.getSeasonsToPlay().peek().getName() + "\n" + game.standingsToString());
    }

    void refreshOrCreateTeamInfo() {
        Map<Team, Standing> teamToStandingMap = game.getTeamToStandingMap();
        Team playingTeam = game.getPlayingTeam();
        String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
        gameUIGenerator.createOrUpdateTextPanel("teamInfo", "Team Info", teamInfo);
    }

    void refreshOrCreateMatchLog(MatchLog matchLog) {
        gameUIGenerator.createOrUpdateTextPanel("matchLog", "Match Log", matchLog.toString());
    }   

    void refreshOrCreatePlayerInfo() {
        gameUIGenerator.createOrUpdateTextPanel("playerInfo", "Player Info", flattenListString(game.getActivePlayers()));
    }
}