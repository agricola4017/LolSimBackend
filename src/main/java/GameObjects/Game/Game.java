package GameObjects.Game;

import GameObjects.Game.GameUI.GameControllerUI;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;
import GameObjects.TeamsAndPlayers.Position;

import javax.swing.*;

import static Functions.Functions.flattenListString;

import java.awt.event.*;

import GameObjects.Game.GameUI.GameUIGeneral;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Game {
    //now that im thinking about it, these should not be static
    static boolean gameIsRunning = true;
    static Queue<Season> seasonsToPlay = new LinkedList<>();
    static List<Team> teams;
    static Map<Integer, Team> teamMap;
    static List<Standing> standings;
    static List<Standing> oldStandings;
    static Map<Team, Standing> teamToStandingMap;
    static List<Player> activePlayers;
    static CountDownLatch latch;
    static Team playingTeam;
    static Map<Integer, Player> playerIDtoPlayerMap;

    static Map<JButton, ActionListener> buttonToActionListenerMap = new HashMap<>();
    
    //win,loss, teamid
    // after update, local sort
    // h2h records
    // kda, etc...

    //self sorting access by ID, but also traverse
    // sort is local
    //stores teamid -> win, loss


    public static void main(String[] args) throws InterruptedException{
        System.out.println("Game is starting");
        /**
         * load players and teams
         */

        activePlayers = new ArrayList<>();
        teams = new ArrayList<>();
        teamMap = new HashMap<Integer, Team>();
        standings = new ArrayList<>();
        teamToStandingMap = new HashMap<>();
        playerIDtoPlayerMap = new HashMap<>();
        latch = new CountDownLatch(0);

        //sql calls to load
        testTeams();
        testPlayers();

        playingTeam = teamMap.get(0);
        //testSignPlayers();
        //Season freeAgency = new freeAgency();
        Season springSplit = new SpringSplit(new ArrayList<>(teams));
        //Season springPlayoffs = new SpringPlayoffs();
        //Season summerSplit = new SummerSplit();
        //Season summerPlayoffs = new SummerPlayoffs();
        //Season Stats = new StatSeason();

        seasonsToPlay.add(springSplit);

        playGame();


        //What does a game need
        //load state, save state

        //assume no load

        //generate free agents
        //generate teams

        //cycle seasons

        //
    }

    static void setupActionListeners(GameControllerUI gameControllerUI, Season currentSeason, int splitCount) {
        JButton playSeasonButton = gameControllerUI.getPlaySeasonButton();
        JButton playGameButton = gameControllerUI.getPlayGameButton();
        JButton seeStandingsButton = gameControllerUI.getSeeStandingsButton();
        JButton seeTeamInfoButton = gameControllerUI.getSeeTeamInfoButton();
        JButton signPlayerButton = gameControllerUI.getSignPlayerButton();

        JButton playTeamGameButton = gameControllerUI.getPlayTeamGameButton();
        JButton findPlayerButton = gameControllerUI.getFindPlayerButton();

        ActionListener playSeasonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(latch.getCount());
                MatchLog matchLog = playSeason(currentSeason, splitCount);
                
                GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);         
                if (matchLog != null)
                    GameUIGeneral.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
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
                    GameUIGeneral.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                    // Also update standings after each game
                    GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                    String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                    GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
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
                GameUIGeneral.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                }    
                // Also update standings after each game
                GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                String teamInfo = teamToStandingMap.get(playingTeam).toString() + "\n" + playingTeam.toString();
                GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);

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
                GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
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
                GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
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
                GameUIGeneral.createPlayerSigningForm((name, position, ovr) -> {
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
                    GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                });
            }
        };
        signPlayerButton.addActionListener(signPlayerListener);
        buttonToActionListenerMap.put(signPlayerButton, signPlayerListener);

        ActionListener findPlayerListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get player search form
                GameUIGeneral.createFindByIDForm("Find Player by ID","Find Player by ID", (playerID) -> {
                    if (!playerIDtoPlayerMap.containsKey(playerID)) {
                        GameUIGeneral.updateIDForm("Find Player by ID", "Player not found");
                        return;
                    }
                    Player player = playerIDtoPlayerMap.get(playerID);
                    GameUIGeneral.updateIDForm("Find Player by ID", player.toString());
                });
                
            }
        };
        findPlayerButton.addActionListener(findPlayerListener);
        buttonToActionListenerMap.put(findPlayerButton, findPlayerListener);
    }

    static void playGame() throws InterruptedException{
        int splitCount = 1;
        GameControllerUI gameControllerUI = new GameControllerUI();
        
        initStandings();
        while(gameIsRunning) {

            Season currentSeason = seasonsToPlay.poll();
            System.out.println(currentSeason.getName() + " " + splitCount);

            latch = new CountDownLatch(1);
            setupActionListeners(gameControllerUI, currentSeason, splitCount);

            latch.await();

            gameControllerUI.getPlayGameButton().removeAll();
            Collections.sort(standings);

            //printStandings();
            standings.get(0).getTeam().addWin();
            for (int i = 0; i < standings.size(); i++) {
                Standing s = standings.get(i);
                s.getTeam().addPlacement(i + 1, splitCount);
            }

            //printStandings();

            cleanupLeague();
            adjustPlayerStats();
            cleanActionListeners();

            splitCount++;
            gameIsRunning = !seasonsToPlay.isEmpty();
        }
    }

    static String printStandings() {
        String ret = "";
        ret += "Final Standings\n";
        
        int j = 1;
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            String standingOutput = j + ". " + standing + " (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " , Prev. Seasons ";
            if (oldStandings != null) {
                standingOutput += oldStandings.get(i);
            } else {
                standingOutput += "0-0";
            }
            ret += standingOutput + "\n";
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

        ret += "Winner is " + standings.get(0);
        return ret;
    }

    static void cleanupLeague() {
        oldStandings = standings;
        teamToStandingMap.clear();

        //remember old teamToStanding as well?

        //repopulate teams in order
        teams.clear();
        for (Standing s : oldStandings) {
            teams.add(s.getTeam());
        }
        
        standings = new ArrayList<>();
        initStandings();
        seasonsToPlay.add(new SpringSplit(teams));
    }

    static MatchLog playSeason(Season currentSeason, int splitCount) {
        MatchLog matchLog = null;
        int count = 1;
        while (!currentSeason.isFinished()) {

            if (count > currentSeason.getOneSetCount()) {
                count = 1;
                //balance teams
                Collections.sort(standings);
                //System.out.println(standings);
            } else {
                matchLog = playMatch(currentSeason, splitCount);
                count++;
            }
        }
        return matchLog;
    }

    static MatchLog playMatch(Season currentSeason, int splitCount) {
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

    static void testTeams() {

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
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        teams.add(team4);
        teams.add(team5);
        teams.add(team6);
        teams.add(team7);
        teams.add(team8);
        teams.add(team9);
        teams.add(team10);
    }

    static void testTeams100() {
        for (int i = 0; i < 100; i++) {
            Team team = new Team(i, "team" + i);
            teamMap.put(i, team);
            teams.add(team);
        }
    }

    static void testPlayers() {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < 5; j++) {
                Player player = Player.generatePlayerWithTeam(i);
                activePlayers.add(player);
                teamMap.get(i).addPlayer(player);
                playerIDtoPlayerMap.put(player.getPlayerID(), player);
            }
            teamMap.get(i).normalizePlayers();
        }
    }

    static void initStandings() {
        for (int i = 0; i < teams.size(); i++) {
            Standing standing = new Standing(teams.get(i));
            standings.add(standing);
            teamToStandingMap.put(teams.get(i), standing);
        }
    }

    static void cleanActionListeners() {
        for (Map.Entry<JButton, ActionListener> entry : buttonToActionListenerMap.entrySet()) {
            entry.getKey().removeActionListener(entry.getValue());
        }
    }

    static void adjustPlayerStats() {
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
}
