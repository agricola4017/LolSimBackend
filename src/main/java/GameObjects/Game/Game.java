package GameObjects.Game;

import GameObjects.Game.MatchesAndSeasons.Match;
import GameObjects.Game.MatchesAndSeasons.MatchLog;
import GameObjects.Game.MatchesAndSeasons.Season;
import GameObjects.Game.MatchesAndSeasons.SpringSplit;
import GameObjects.Game.MatchesAndSeasons.SpringPlayoffs;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;
import GameObjects.Game.History.History;

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
    private List<Player> teamlessPlayers; 

    private Map<Integer, Team> teamIDtoTeamMap; //specifically gameplay ones that will be loaded and saved 
    private Map<Integer, Player> playerIDtoPlayerMap;


    //game state objects that represent current season
    private CountDownLatch latch;

    //UI object reference 
    private Map<JButton, ActionListener> buttonToActionListenerMap = new HashMap<>();

    private Team playingTeam;

    private History history;

    private Season currentSeason;

    public Game() {
        this.activePlayers = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamIDtoTeamMap = new HashMap<Integer, Team>();
        this.playerIDtoPlayerMap = new HashMap<>();
        this.latch = new CountDownLatch(0);
        this.gameIsRunning = true;
        this.initTeamlessPlayers();
        //would be cool if you could force a load if you did this constructor, maybe a factory is necessary for this
    }

    public Game(Map<Integer, Team> teamIDtoTeamMap, Map<Integer, Player> playerIDtoPlayerMap, Team playingTeam, CountDownLatch latch) {
        this.teamIDtoTeamMap = teamIDtoTeamMap;
        this.playerIDtoPlayerMap = playerIDtoPlayerMap;
        this.playingTeam = playingTeam;
        this.latch = latch;

        this.teams = new ArrayList<>(teamIDtoTeamMap.values());
        this.activePlayers = new ArrayList<>(playerIDtoPlayerMap.values());
        this.initTeamlessPlayers();
    }

    public void loadGame(boolean gameIsRunning, Queue<Season> seasonsToPlay, List<Team> teams, List<Player> activePlayers, 
                         Map<Integer, Team> teamIDtoTeamMap, Map<Integer, Player> playerIDtoPlayerMap, Team playingTeam) throws IOException { 

        this.gameIsRunning = gameIsRunning;
        this.seasonsToPlay = seasonsToPlay;
        this.teams = teams;
        this.activePlayers = activePlayers;
        this.teamIDtoTeamMap = teamIDtoTeamMap;
        this.playerIDtoPlayerMap = playerIDtoPlayerMap;
        this.playingTeam = playingTeam;
        this.history = new History(1);

        this.currentSeason = seasonsToPlay.peek();
        this.initTeamlessPlayers();
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

    Season getCurrentSeason() {
        return seasonsToPlay.peek();
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

    boolean getGameIsRunning() {
        return gameIsRunning;
    }

    History getHistory() {
        return history;
    }
    /**
     * After game data has been loaded or initialized, this method is called to start the first season of the game.
     * It should be expected that in future development, there will be more types of seasons and progressions.
     * Depending on user configuration, the progression should be changed.
     * @throws InterruptedException
     */
    void loadGameWithSeasonsConfig() throws InterruptedException {
        activePlayers.sort(Comparator.comparingInt(Player::getOVR));
        
        Season springSplit = new SpringSplit(teams);
        seasonsToPlay.add(springSplit);
    }

    /**
     * Main game loop. 
     * Counts the number of seasons that have progressed and pauses the game thread for the EDT waiting for user input.
     * @throws InterruptedException
     */
    void playGame() throws InterruptedException{
        int splitCount = 1;
        this.gameIsRunning = true;
        this.history = new History(splitCount);
        
        while(gameIsRunning) {
            currentSeason = prepareNewSeason(splitCount);
            
            latch.countDown();
            latch = new CountDownLatch(1);
            latch.await();

            postSeasonCleanup(splitCount, currentSeason);
            if (currentSeason instanceof SpringPlayoffs) {
                splitCount++;
            }
            gameIsRunning = !seasonsToPlay.isEmpty();
        }
    }
    
    /**
     * prepares a new season to be played
     * * initializes the standings, clears action listeners, and grabs current season
     * @param splitCount
     */
    Season prepareNewSeason(int splitCount) {
        Season currentSeason = seasonsToPlay.peek();
        System.out.println(currentSeason.getName() + " " + splitCount);

        latch = new CountDownLatch(1);

        return currentSeason;
    }

    /**
     * OVR Standings
     * @param splitCount
     */
    String getOVRStandings() {
        String ret = "OVR Standings\n";
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

    void initTeamlessPlayers() {
        teamlessPlayers = new ArrayList<>();
        for (Player player: activePlayers) {
            if (player.getTeamID() == -1) {
                teamlessPlayers.add(player);
            }
        }
    }

    /**
     * cleans up the game after a season has finished
     * * sorts the standings, records the placements, clears the league, and runs player progression, and cleans up action listeners
     * * signs new players to teams, and generates new players for the next season 
     * @param splitCount
     */
    void postSeasonCleanup(int splitCount, Season currentSeason) {
        currentSeason.postSeasonCleanup(splitCount);

        repopulateTeamInOrderOfStandings(currentSeason);
        adjustPlayerStats();
    
        activePlayers.sort(Comparator.comparingInt(Player::getOVR));
        history.recordSeason(currentSeason);
        if (currentSeason instanceof SpringPlayoffs) {
            generatePlayers();
            //mockFreeAgency(currentSeason);
            //retirePlayers();
        }
        Season season = seasonsToPlay.poll();
        seasonsToPlay.add(season.generateNextSeason(teams));
        //cleanActionListeners();
    }

    void generatePlayers() {
        for (int i = 0; i < 50; i++) {
            Player player = Player.generatePlayer();
            activePlayers.add(player);
        }
        initTeamlessPlayers();
    }

    void mockFreeAgency(Season currentSeason) {
        //generates some new players 
        // there are 10 teams, so let's just generate 50 new players 
        for (Team team: teams) {
            team.normalizePlayers();
            
            for (Player player: team.getPlayers()) {
                player.addYearsWithTeam();
            }

            for (Player player: team.getPlayerRoster().getActivePlayers().values()) {
                player.addYearsStartingWithTeam();
            }

            //algo should compare all possible activeRoster players one by one, measure largest OVR delta, and if there is no player signable, sign youngest player that is the best replacement 
            for (int playerIndex = 0; playerIndex < teamlessPlayers.size(); playerIndex++) {
                Player player = teamlessPlayers.get(playerIndex);
                Boolean noPlayerInPosition = team.getPlayerRoster().getPlayerByPosition(player.getPosition()) == null;
                Boolean playerInRosterWeaker = team.getPlayerRoster().getPlayerByPosition(player.getPosition()).getOVR() < player.getOVR();
                if (noPlayerInPosition || playerInRosterWeaker) {
                    team.addPlayer(player);
                    teamlessPlayers.remove(player);
                    playerIndex--;
                    break;
                }
            }
            team.normalizePlayers();

            //if team has more than 10 players, remove player with lowest POT
            if (team.getPlayers().size() > 10) {
                List<Player> nonRosterPlayers = team.getNonRosterPlayers();
                //not sure why this would ever throw null, but check on this later
                Player worst = nonRosterPlayers.stream().min(Comparator.comparingInt(p -> p.getStat().getPotential())).get();
                team.removePlayer(worst);
                teamlessPlayers.add(worst);
                worst.resetYearsStartingWithTeam();
                worst.resetYearsWithTeam();
            }
        }
    }

    void retirePlayers() {
        if (teamlessPlayers.size() > 100) {
            teamlessPlayers.sort(Comparator.comparingInt(p -> p.getStat().getPotential()));

            for (int i = 0; i < 10; i++) {
                teamlessPlayers.remove(0);
                activePlayers.remove(0);
            }
        }
    }

    void repopulateTeamInOrderOfStandings(Season currentSeason) {
        List<Team> oldTeams = new ArrayList<>(teams);
        teams = currentSeason.repopulateTeamInOrderOfStandings();
        if (teams.size() != oldTeams.size()) {
            for (int i = teams.size(); i < oldTeams.size(); i++) {
                teams.add(oldTeams.get(i));
            }
        }
    }

    /**
     * Plays the current season and returns the match log
     * @param currentSeason: the season to play
     * @return the match log of the season
     */
    MatchLog playSeason(Season currentSeason) {
        MatchLog matchLog = null;
        //int count = 1;
        //this validation should be moved to the controller 
        while (!currentSeason.isFinished()) {

            matchLog = playMatch(currentSeason);
            /** 
            if (count > currentSeason.getOneSetCount()) {
                count = 1;
                //balance teams
                Collections.sort(standings);
            } else {
                
                count++;
            }
            */
        }
        System.out.println("Season finished");
        System.out.println(currentSeason.getWinner().getTeamName() + " won the season!");
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

    private void disableActionListeners() {
        for (Map.Entry<JButton, ActionListener> entry : buttonToActionListenerMap.entrySet()) {
            entry.getKey().setEnabled(false);
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
