package Game;

import GameObjects.MatchesAndSeasons.MatchLog;
import GameObjects.MatchesAndSeasons.Season;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Standing;
import GameObjects.TeamsAndPlayers.Team;
import java.util.List;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import Functions.Functions;
import Game.GameUI.GameControllerUI;
import Game.GameUI.GameUIGenerator;

import java.awt.event.ActionListener;

import static Functions.Functions.flattenListString;

import java.awt.event.ActionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingWorker;
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
    private JButton seeHistoryButton;
    private GameUIGenerator gameUIGenerator;

    private static final String TEAMINFO_PANELLID = "teamInfo";
    private static final String PLAYERINFO_PANELLID = "playerInfo";
    private static final String MATCHLOG_PANELID = "matchLog";
    private static final String STANDINGS_PANELID = "standings";
    private static final String HISTORY_PANELID = "history";

    private CountDownLatch latch;

    public UIGameService(Game game, GameControllerUI gameControllerUI, CountDownLatch latch) {
        this.playSeasonButton = gameControllerUI.getPlaySeasonButton();
        this.playGameButton = gameControllerUI.getPlayGameButton();
        this.playTeamGameButton = gameControllerUI.getPlayTeamGameButton();
        this.seeStandingsButton = gameControllerUI.getSeeStandingsButton();
        this.seeTeamInfoButton = gameControllerUI.getSeeTeamInfoButton();
        this.signPlayerButton = gameControllerUI.getSignPlayerButton();
        this.findPlayerButton = gameControllerUI.getFindPlayerButton();
        this.seePlayersButton = gameControllerUI.getSeePlayersButton();
        this.findTeamButton = gameControllerUI.getFindTeamButton();
        this.saveGameButton = gameControllerUI.getSaveGameButton();
        this.loadGameButton = gameControllerUI.getLoadGameButton();
        this.seeHistoryButton = gameControllerUI.getSeeHistoryButton();
        this.game = game;
        this.gameUIGenerator = new GameUIGenerator();
        this.latch = latch;
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

                playSeasonButton.setEnabled(false);

                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    Season currentSeason;
                    MatchLog matchLog;

                    @Override
                    protected Void doInBackground() throws Exception {  
                        currentSeason = game.getSeasonsToPlay().peek();
                        matchLog = game.playSeason(currentSeason);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        refreshOrCreateStandings(currentSeason);
                        refreshOrCreateHistory();
                        if (matchLog != null)
                            refreshOrCreateMatchLog(matchLog);
                        try {
                            game.cleanUpPostSeasonAndPrepareForNewSeason();
                            game.countDownLatch();
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted");
                        }
                        playSeasonButton.setEnabled(true);
                    }
                };
                swingWorker.execute();
            }
        };

        buttonToActionListenerMap.put(playSeasonButton, playSeasonListener);
        playSeasonButton.addActionListener(playSeasonListener);


        ActionListener playGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {   
                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    Season currentSeason;
                    MatchLog matchLog;
                    @Override
                    protected Void doInBackground() throws Exception {  
                        currentSeason = game.getSeasonsToPlay().peek();   
                        matchLog = game.playMatch(currentSeason, false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        refreshOrCreateMatchLog(matchLog);
                        refreshOrCreateStandings(currentSeason);
                        refreshOrCreateHistory();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted");
                        }
        
                        if (currentSeason.isFinished()) {
                            game.cleanUpPostSeasonAndPrepareForNewSeason();
                            game.countDownLatch();
                        }
                    }
                };
                swingWorker.execute();
            }
        };

        
        playGameButton.addActionListener(playGameListener);
        buttonToActionListenerMap.put(playGameButton, playGameListener);

        ActionListener playTeamGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
                    MatchLog matchLog;
                    Boolean teamGamePlayable;
                    Season currentSeason;
                    Team playingTeam;

                    @Override
                    protected Void doInBackground() throws Exception {
                        currentSeason = game.getSeasonsToPlay().peek();
                        playingTeam = game.getPlayingTeam();
                        teamGamePlayable = currentSeason.containsTeam(playingTeam);

                        if (teamGamePlayable) {
                            do {
                                matchLog = game.playMatch(currentSeason, false);
                            } while (matchLog != null && (matchLog.getWinner() != playingTeam && matchLog.getLoser() != playingTeam));
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (teamGamePlayable) {
                            if (matchLog != null) {
                                refreshOrCreateMatchLog(matchLog);
                            }    
                            // Also update standings after each game
                            refreshOrCreateStandings(currentSeason);
                            refreshOrCreateTeamInfo();
                        }
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted");
                        }
    
                        if (currentSeason.isFinished()) {
                            game.cleanUpPostSeasonAndPrepareForNewSeason();
                            game.countDownLatch();
                        }
                    }
                };
                swingWorker.execute();
            }
        };

        buttonToActionListenerMap.put(playTeamGameButton, playTeamGameListener);
        playTeamGameButton.addActionListener(playTeamGameListener);
        

        ActionListener seeStandingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshOrCreateStandings(game.getSeasonsToPlay().peek());
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
                    oos.writeObject(game.getPlayingTeam());

                    System.out.println("Game successfully saved");
                } catch (IOException ex) {
                    System.out.println("Error saving game :" + ex.getMessage() + ex.getStackTrace());
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
                    Team playingTeam = (Team) ois.readObject();
                    game.loadGame(gameIsRunning, seasonsToPlay, teams, activePlayers, teamIDtoTeamMap, playerIDtoPlayerMap, playingTeam);
                    System.out.println("Game Successfully Loaded");
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Error loading game: " + ex.getMessage());
                } 
                    // Also update standings after each game
                refreshOrCreateStandings(game.getSeasonsToPlay().peek());
                refreshOrCreateTeamInfo();
            }
        };
        loadGameButton.addActionListener(loadGameListener);
        buttonToActionListenerMap.put(loadGameButton, loadGameListener);

        ActionListener seeHistoryListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // See history
                refreshOrCreateHistory();
            }
        };
        seeHistoryButton.addActionListener(seeHistoryListener);
        buttonToActionListenerMap.put(seeHistoryButton, seeHistoryListener);
    }

    void refreshOrCreateHistory() {
        gameUIGenerator.createOrUpdateTextPanel(HISTORY_PANELID, "History", game.getHistory().getLeagueHistory());
    }
    void refreshOrCreateStandings(Season currentSeason) {
        String winner = "";
        winner = currentSeason != null && currentSeason.isFinished() ? currentSeason.getName() + " Winner: " + currentSeason.getWinner().getTeamName() : currentSeason.getName() + " is in progress";
        gameUIGenerator.createOrUpdateTextPanel(STANDINGS_PANELID, "Standings", 
            winner + "\n" + game.getCurrentSeason().toString());
    }

    void refreshOrCreateTeamInfo() {
        Team playingTeam = game.getPlayingTeam();
        Standing standing = game.getCurrentSeason().getStanding(playingTeam);
        String teaminfo;

        //if didn't make playoffs
        if (standing == null) {
            teaminfo = playingTeam.toString();
        }
        else {
            teaminfo = standing.toString() + "\n" + playingTeam.toString();
        }
        gameUIGenerator.createOrUpdateTextPanel(TEAMINFO_PANELLID, "Team Info", teaminfo);
    }

    void refreshOrCreateMatchLog(MatchLog matchLog) {
        gameUIGenerator.createOrUpdateTextPanel(MATCHLOG_PANELID, "Match Log", matchLog.toString());
    }   

    void refreshOrCreatePlayerInfo() {
        gameUIGenerator.createOrUpdateTextPanel(PLAYERINFO_PANELLID, "Player Info", Player.toStringHeaders() + "\n" + flattenListString(game.getActivePlayers()));
    }

}