package GameObjects.Game;

import GameObjects.Game.GameUI.GameControllerUI;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import javax.swing.*;

import static Functions.Functions.flattenListString;

import java.awt.event.*;

import GameObjects.Game.GameUI.GameUIGenerator;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Game {

    //not implemented yet what to decide game is not running anymore 
    private boolean gameIsRunning;
    private Queue<Season> seasonsToPlay = new LinkedList<>();

    //game state objects that represent current game 
    private List<Team> teams;
    private List<Player> activePlayers;

    private Map<Integer, Team> teamIDtoTeamMap; //specifically gameplay ones that will be loaded and saved 
    private Map<Integer, Player> playerIDtoPlayerMap;

    private List<Standing> standings;
    private List<Standing> oldStandings;
    private Map<Team, Standing> teamToStandingMap;
    

    //game state objects that represent current season
    private CountDownLatch latch;

    //UI object reference 
    private Map<JButton, ActionListener> buttonToActionListenerMap = new HashMap<>();

    private Team playingTeam;

    public Game() {
        this.activePlayers = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamIDtoTeamMap = new HashMap<Integer, Team>();
        this.standings = new ArrayList<>();
        this.teamToStandingMap = new HashMap<>();
        this.playerIDtoPlayerMap = new HashMap<>();
        this.latch = new CountDownLatch(0);
        this.gameIsRunning = true;

        //would be cool if you could force a load if you did this constructor, maybe a factory is necessary for this
    }

    public Game(Map<Integer, Team> teamIDtoTeamMap, Map<Integer, Player> playerIDtoPlayerMap, Team playingTeam) {
        this.teamIDtoTeamMap = teamIDtoTeamMap;
        this.playerIDtoPlayerMap = playerIDtoPlayerMap;
        this.playingTeam = playingTeam;

        this.teams = new ArrayList<>(teamIDtoTeamMap.values());
        this.activePlayers = new ArrayList<>(playerIDtoPlayerMap.values());

        this.latch = new CountDownLatch(0);
        this.gameIsRunning = true;
        this.standings = new ArrayList<>();
        this.teamToStandingMap = new HashMap<>();
    }

    //getters and setters

    List<Player> getPlayers() {
        return activePlayers;   
    }

    List<Team> getTeams() {
        return teams;
    }

    Map<Integer, Team> getTeamIDtoTeamMap() {
        return teamIDtoTeamMap;
    }

    Map<Team, Standing> getTeamToStandingMap() {
        return teamToStandingMap;
    }

    List<Standing> getStandings() {
        return standings;
    }

    List<Player> getActivePlayers() {
        return activePlayers;
    }

    Map<Integer, Player> getPlayerIDtoPlayerMap() {
        return playerIDtoPlayerMap;
    }

    void sortActivePlayers() {
        activePlayers.sort(Comparator.comparingInt(Player::getOVR));
    }

    /**
     * After game data has been loaded or initialized, this method is called to start the first season of the game.
     * It should be expected that in future development, there will be more types of seasons and progressions.
     * Depending on user configuration, the progression should be changed.
     * @throws InterruptedException
     */
    void loadGameWithSeasonsConfig() throws InterruptedException {
        activePlayers.sort(Comparator.comparingInt(Player::getOVR));
        
        Season springSplit = new SpringSplit(new ArrayList<>(teams));
        seasonsToPlay.add(springSplit);
    }

    /**
     * Main game loop. 
     * Counts the number of seasons that have progressed and pauses the game thread for the EDT waiting for user input.
     * @throws InterruptedException
     */
    void playGame() throws InterruptedException{
        int splitCount = 1;
        GameControllerUI gameControllerUI = new GameControllerUI();
        
        while(gameIsRunning) {
            prepareNewSeason(gameControllerUI, splitCount);

            latch.await();

            //gameControllerUI.getPlayGameButton().removeAll();

            postSeasonCleanup(splitCount);
            splitCount++;
            gameIsRunning = !seasonsToPlay.isEmpty();
        }
    }
    
    /**
     * prepares a new season to be played
     * * initializes the standings, clears action listeners, and grabs current season
     * @param gameControllerUI
     * @param splitCount
     */
    void prepareNewSeason(GameControllerUI gameControllerUI, int splitCount) {
        initStandings();
        Season currentSeason = seasonsToPlay.poll();
        System.out.println(currentSeason.getName() + " " + splitCount);

        latch = new CountDownLatch(1);

        setupActionListeners(gameControllerUI, currentSeason, splitCount);
        //maybe pass these vars to controller 
    }

    /**
     * cleans up the game after a season has finished
     * * sorts the standings, records the placements, clears the league, and runs player progression, and cleans up action listeners
     * @param splitCount
     */
    void postSeasonCleanup(int splitCount) {
        Collections.sort(standings);

        standings.get(0).getTeam().addWin();
        for (int i = 0; i < standings.size(); i++) {
            Standing s = standings.get(i);
            s.getTeam().addPlacement(i + 1, splitCount);
        }

        cleanupLeague();
        seasonsToPlay.add(new SpringSplit(teams));
        adjustPlayerStats();
        activePlayers.sort(Comparator.comparingInt(Player::getOVR));
        cleanActionListeners();
    }

    /**
     * Initializes the standings based on the teams in the league
     */
    void initStandings() {
        for (int i = 0; i < teams.size(); i++) {
            Standing standing = new Standing(teams.get(i));
            standings.add(standing);
            teamToStandingMap.put(teams.get(i), standing);
        }
    }

    /**
     * this should likely be its own class
     * @param gameControllerUI
     * @param currentSeason
     * @param splitCount
     */ 
    void setupActionListeners(GameControllerUI gameControllerUI, Season currentSeason, int splitCount) {
        JButton playSeasonButton = gameControllerUI.getPlaySeasonButton();
        JButton playGameButton = gameControllerUI.getPlayGameButton();
        JButton seeStandingsButton = gameControllerUI.getSeeStandingsButton();
        JButton seeTeamInfoButton = gameControllerUI.getSeeTeamInfoButton();
        JButton signPlayerButton = gameControllerUI.getSignPlayerButton();

        JButton playTeamGameButton = gameControllerUI.getPlayTeamGameButton();
        JButton findPlayerButton = gameControllerUI.getFindPlayerButton();
        JButton seePlayersButton = gameControllerUI.getSeePlayersButton();
        JButton findTeamButton = gameControllerUI.getFindTeamButton();

        ActionListener playSeasonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatchLog matchLog = playSeason(currentSeason, splitCount);
                
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", standingsToString());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);         
                if (matchLog != null)
                    GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                try {
                    Thread.sleep(100);
                    latch.countDown();
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
                MatchLog matchLog = playMatch(currentSeason, splitCount);
                if (matchLog != null) {
                    GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                    // Also update standings after each game
                    GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", standingsToString());
                    String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                    GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                if (currentSeason.isFinished()) {
                    latch.countDown();
                }
            }
        };

        
        playGameButton.addActionListener(playGameListener);
        buttonToActionListenerMap.put(playGameButton, playGameListener);

        ActionListener playTeamGameListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatchLog matchLog;
                do {
                    matchLog = playMatch(currentSeason, splitCount);
                } while (matchLog != null && (matchLog.getWinner() != playingTeam && matchLog.getLoser() != playingTeam));
                
                if (matchLog != null) {
                GameUIGenerator.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                }    
                // Also update standings after each game
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", standingsToString());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGenerator.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                if (currentSeason.isFinished()) {
                    latch.countDown();
                }
            }           
        };

        buttonToActionListenerMap.put(playTeamGameButton, playTeamGameListener);
        playTeamGameButton.addActionListener(playTeamGameListener);
        

        ActionListener seeStandingsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameUIGenerator.createOrUpdateTextFrame("standings", "Standings", standingsToString());
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
            @Override
            public void actionPerformed(ActionEvent e) {
                GameUIGenerator.createOrUpdateTextFrame("players", "Players", flattenListString(activePlayers));
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
    }

    /**
     *  Returns a string representation of the standings to be displayed in UI 
     * @return the standings string
     */
    String standingsToString() {
        String ret = "";
        
        int j = 1;
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            String standingOutput = j + ". " + standing + " (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " , Prev. Seasons ";
            if (oldStandings != null) {
                standingOutput += oldStandings.get(i);
            } else {
                standingOutput += "0-0";
            }
            ret += standingOutput + ", teamID: " + standing.getTeam().getTeamID() + "\n";
            j++;
        }
        ret += "OVR Standings\n";
        SortedMap<Integer, List<Team>> ovrToTeamMap = new TreeMap<>(Collections.reverseOrder());
        for (Team team : teams) {
            if (ovrToTeamMap.containsKey(team.getPlayerRoster().getOVR())) {
                ovrToTeamMap.get(team.getPlayerRoster().getOVR()).add(team);
            } else {
                List<Team> teamlistInMap = new ArrayList<>();
                teamlistInMap.add(team);
                ovrToTeamMap.put(team.getPlayerRoster().getOVR(), teamlistInMap);
            }
        }

        int i = 1;
        for (SortedMap.Entry<Integer, List<Team>> entry : ovrToTeamMap.entrySet()) {
            for (Team team : entry.getValue()) {
                ret += i + ". " + team.getTeamName() + " " + entry.getKey() + "\n";
                i++;
            }
        }

        return ret;
    }

    /**
     * cleans and clears the league: 
     * * * teamToStandingMap
     * * * teams
     * * * standings
     * also records previous season's standings
     */
    void cleanupLeague() {
        oldStandings = standings;
        teamToStandingMap.clear();

        //remember old teamToStanding as well?

        //repopulate teams in order
        teams.clear();
        for (Standing s : oldStandings) {
            teams.add(s.getTeam());
        }
        
        standings = new ArrayList<>();
    }

    /**
     * Plays the current season and returns the match log
     * @param currentSeason: the season to play
     * @param splitCount: the split count of the season
     * @return the match log of the season
     */
    MatchLog playSeason(Season currentSeason, int splitCount) {
        MatchLog matchLog = null;
        int count = 1;
        //this validation should be moved to the controller 
        while (!currentSeason.isFinished()) {

            if (count > currentSeason.getOneSetCount()) {
                count = 1;
                //balance teams
                Collections.sort(standings);
            } else {
                matchLog = playMatch(currentSeason, splitCount);
                count++;
            }
        }
        return matchLog;
    }

    /**
     * Plays a match and returns the match log
     * @param currentSeason: the season to play the match from
     * @param splitCount: the split count of the season
     * @return the match log of the match
     */
    MatchLog playMatch(Season currentSeason, int splitCount) {
        //validation should be moved to the controller 
        if (!currentSeason.isFinished()) {
            Match match = currentSeason.playMatch();
            //System.out.println(match);

            Team winner = match.getMatchLog().getWinner();
            Team loser = match.getMatchLog().getLoser();

            teamToStandingMap.get(winner).wonGame();
            teamToStandingMap.get(loser).lostGame();
            Collections.sort(standings);
            //winner.setStanding(teamToStandingMap.get(winner)
            return match.getMatchLog();
        } else {
            return null;
        }
    }

    /**
     * Removes all action listeners from the buttons
     */
    void cleanActionListeners() {
        for (Map.Entry<JButton, ActionListener> entry : buttonToActionListenerMap.entrySet()) {
            entry.getKey().removeActionListener(entry.getValue());
        }
    }

    /**
     * Adjusts the player stats based on age
     */
    void adjustPlayerStats() {
        for (Player player : activePlayers) {
            int age = player.getAge();
            player.setAge(age + 1);
            int improvementChance = 0;
            int declineChance = 0;
        
            if (age < 17) {
                improvementChance = 80; // Very high chance for ages below 17
                declineChance = 10;
            } else if (age <= 20) {
                improvementChance = 70 - ((age - 17) * 10);
                declineChance = 20 + ((age - 17) * 5);
            } else if (age <= 25) {
                improvementChance = 50 - ((age - 20) * 10);
                declineChance = 30 + ((age - 20) * 5);
            } else if (age <= 30) {
                improvementChance = 30 - ((age - 25) * 5);
                declineChance = 50 + ((age - 25) * 10);
            }
        
            int randomValue = (int) (Math.random() * 100); // Generate random number between 0 and 99
        
            if (randomValue < improvementChance) {
                player.increaseStats(); // Method to increase player's stats
            } else if (randomValue < (improvementChance + declineChance)) {
                player.decreaseStats(); // Method to decrease player's stats
            }

            player.resetSeasonStats();
        }
    }

    //temp function for later UI selection
    void selectTeam(Team team) {
        playingTeam = team;
    }
}
