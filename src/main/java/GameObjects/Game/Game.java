package GameObjects.Game;

import GameObjects.Game.MatchesAndSeasons.Match;
import GameObjects.Game.MatchesAndSeasons.MatchLog;
import GameObjects.Game.MatchesAndSeasons.Season;
import GameObjects.Game.MatchesAndSeasons.SpringSplit;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Standing;
import GameObjects.TeamsAndPlayers.Team;

import javax.swing.*;

import java.awt.event.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import java.io.*;

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

    public void loadGame(boolean gameIsRunning, Queue<Season> seasonsToPlay, List<Team> teams, List<Player> activePlayers, 
                         Map<Integer, Team> teamIDtoTeamMap, Map<Integer, Player> playerIDtoPlayerMap, List<Standing> standings, List<Standing> oldStandings, 
                         Map<Team, Standing> teamToStandingMap, Team playingTeam) throws IOException { 

        this.gameIsRunning = gameIsRunning;
        this.seasonsToPlay = seasonsToPlay;
        this.teams = teams;
        this.activePlayers = activePlayers;
        this.teamIDtoTeamMap = teamIDtoTeamMap;
        this.playerIDtoPlayerMap = playerIDtoPlayerMap;
        this.standings = standings;
        this.oldStandings = oldStandings;
        this.teamToStandingMap = teamToStandingMap;
        this.playingTeam = playingTeam;
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

    Queue<Season> getSeasonsToPlay() {
        return seasonsToPlay;
    }

    CountDownLatch getLatch() {
        return latch;
    }

    Team getPlayingTeam() {
        return playingTeam;
    }

    void countDownLatch() {
        latch.countDown();
    }

    List<Standing> getOldStandings() {
        return oldStandings;
    }

    boolean getGameIsRunning() {
        return gameIsRunning;
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
       // GameControllerUI gameControllerUI = new GameControllerUI();
        
        while(gameIsRunning) {
            prepareNewSeason(splitCount);

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
     * @param splitCount
     */
    void prepareNewSeason(int splitCount) {
        initStandings();
        Season currentSeason = seasonsToPlay.peek();
        System.out.println(currentSeason.getName() + " " + splitCount);

        latch = new CountDownLatch(1);

        //setupActionListeners(gameControllerUI, currentSeason);
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
        seasonsToPlay.poll();
        //cleanActionListeners();
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
     *  Returns a string representation of the standings to be displayed in UI 
     * @return the standings string
     */
    String standingsToString() {
        String ret = "";
        
        int j = 1;
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            String standingOutput = j + ". " + standing + " (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " | Prev. ";
            if (oldStandings != null) {
                standingOutput += oldStandings.get(i);
            } else {
                standingOutput += "0-0";
            }
            ret += standingOutput + " | TID:" + standing.getTeam().getTeamID() + "\n";
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
     * @return the match log of the season
     */
    MatchLog playSeason(Season currentSeason) {
        MatchLog matchLog = null;
        int count = 1;
        //this validation should be moved to the controller 
        while (!currentSeason.isFinished()) {

            if (count > currentSeason.getOneSetCount()) {
                count = 1;
                //balance teams
                Collections.sort(standings);
            } else {
                matchLog = playMatch(currentSeason);
                count++;
            }
        }
        return matchLog;
    }

    /**
     * Plays a match and returns the match log
     * @param currentSeason: the season to play the match from
     * @return the match log of the match
     */
    MatchLog playMatch(Season currentSeason) {
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
            int improvementChance = 0;
            int declineChance = 0;
        
            if (age <= 17) {
                //ranges 17 to 19
                improvementChance = 60; // Very high chance for ages below 17
                declineChance = 30;
            } else if (age <= 20) {
                //ranges 20 to 24 
                //improve is 50 to 42
                //decline is 30 to 34
                improvementChance = 50 - ((age - 20) * 2);
                declineChance = 30 + ((age - 20) * 1);
            } else if (age <= 25) {
                //ranges 25 to 29
                //improve is 40 to 32
                //decline is 30 to 32
                improvementChance = 40 - ((age - 25) * 2);
                declineChance = (int)Math.round(30 + ((age - 25) * 0.5));
            } else if (age <= 30) {
                //ranges 30 to 34
                //improve is 30 to 26
                //decline is 24 to 26
                improvementChance = 30 - ((age - 30) * 1);
                declineChance = 24 + (int)Math.round(((age - 30) * 0.5));
            } else {
                improvementChance = 15;
                declineChance = 18;
            }

            player.setAge(age + 1);
            
            player.changeStats(improvementChance, declineChance);

            player.resetSeasonStats();
        }
    }

    //temp function for later UI selection
    void selectTeam(Team team) {
        playingTeam = team;
    }
}
