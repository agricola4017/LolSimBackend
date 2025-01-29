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
    static boolean gameIsRunning = true;
    static Queue<Season> seasonsToPlay = new LinkedList<>();
    static List<Team> teams;
    static Map<Integer, Team> teamMap;
    static List<Standing> standings;
    static List<Standing> oldStandings;
    static Map<Team, Standing> teamToStandingMap;
    static List<Player> activePlayers;
    static CountDownLatch latch;
    static int latch_flag;
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
        latch = new CountDownLatch(latch_flag);
        latch_flag = 0;

        //sql calls to load
        testTeams();
        testPlayers();

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

        playSeasonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSeason(currentSeason, splitCount);
                // Update standings after playing season
                GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        });

        playGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatchLog matchLog = playMatch(currentSeason, splitCount);
                if (matchLog != null) {
                    GameUIGeneral.createOrUpdateTextFrame("matchLog", "Match Log", matchLog.toString());
                    // Also update standings after each game
                    GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        });

        seeStandingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameUIGeneral.createOrUpdateTextFrame("standings", "Standings", printStandings());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        });

        seeTeamInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String teamInfo = teamToStandingMap.get(teams.get(0)).toString() + "\n" + 
                    teams.get(0).toString();
                GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }
            }
        });

        signPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get player signing form
                GameUIGeneral.createPlayerSigningForm((name, position, ovr) -> {
                    Player player = Player.generateNamedPlayerFromOVRandPosition(
                        name, 
                        ovr, 
                        teams.get(0).getTeamID(), 
                        position
                    );
                    activePlayers.add(player);
                    teams.get(0).addPlayer(player);
                    teams.get(0).normalizePlayers();
                    String teamInfo = teamToStandingMap.get(teams.get(0)).toString() + "\n" + teams.get(0).toString();
                    GameUIGeneral.createOrUpdateTextFrame("teamInfo", "Team Info", teamInfo);
                });
            }
        });
    }

    static void awaitPlayerPrompt(int caseValue, int splitCount, Season currentSeason) throws InterruptedException{

        switch(caseValue) {
            case 1:
                //play season
                playSeason(currentSeason, splitCount);
                break;
            case 2:
                //play game
                MatchLog matchLog = playMatch(currentSeason, splitCount);
                if (matchLog != null) {
                    System.out.println("Match is finished");
                    //GameUIGeneral.createOrUpdateTextFrame("match","Match is finished", matchLog.toString());
                    System.out.println(matchLog.toString());
                } else {
                    System.out.println("Season is finished");
                }
                break;
            case 3:
                //see team info
                //GameUIGeneral.createOrUpdateTextFrame("Team Info and Standings", teamToStandingMap.get(teams.get(0)).toString() + "\n" + teams.get(0).toString());
                break;
            case 4:
                //see standings
                Collections.sort(standings);
                printStandings();
                break;
            case 5:
                //see players
                //GameUIGeneral.createOrUpdateTextFrame("Players", flattenListString(teams.get(0).getPlayers()));
                break;
            case 6:
                //sign player
                
                GameUIGeneral.createPlayerSigningForm((name, position, ovr) -> {
                    Player player = Player.generateNamedPlayerFromOVRandPosition(
                        name, 
                        ovr, 
                        teams.get(0).getTeamID(), 
                        position
                    );
                    activePlayers.add(player);
                    teams.get(0).addPlayer(player);
                });

                /** 
                Scanner scanner = new Scanner(System.in);
                System.out.println("Design a player to sign");
                System.out.println("---------------------------");
                System.out.println("Enter player name: ");
                String name = scanner.nextLine();
                System.out.println("Enter player position: ");
                String positionString = scanner.nextLine();
                Position position = Position.valueOf(positionString);
                System.out.println("Enter player OVR: ");
                int ovr = scanner.nextInt();
                Player player = Player.generateNamedPlayerFromOVRandPosition(name, ovr, teams.get(0).getTeamID(), position);
                activePlayers.add(player);
                teams.get(0).addPlayer(player);
                scanner.close();
                */
                break;
            case 7:
                //see active players
                System.out.println(activePlayers);
                break;
        }
         

    }

    static void playGame() throws InterruptedException{
        int splitCount = 0;
        GameControllerUI gameControllerUI = new GameControllerUI();
        while(gameIsRunning) {

            //initialize standings
            initStandings();

            Season currentSeason = seasonsToPlay.poll();
            System.out.println(currentSeason.getName() + " " + splitCount);
        
            setupActionListeners(gameControllerUI, currentSeason, splitCount);

            System.out.println("1: play season\n2: play game\n3: see team info\n4: see standings\n5: see players\n6: sign player\n7: see active players");
            while (!currentSeason.isFinished()) {
                latch = new CountDownLatch(1);
                latch_flag=1;
        
                latch.await();
            }

            Collections.sort(standings);

            printStandings();

            cleanupLeague();

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
        standings = new ArrayList<>();
        teamToStandingMap = new HashMap<>();

        //remember old teamToStanding as well?

        //repopulate teams in order
        teams.clear();
        for (Standing s : oldStandings) {
            teams.add(s.getTeam());
        }

        seasonsToPlay.add(new SpringSplit(teams));
    }

    static void playSeason(Season currentSeason, int splitCount) {

        int count = 1;
        while (!currentSeason.isFinished()) {

            if (count > currentSeason.getOneSetCount()) {
                count = 1;
                //balance teams
                Collections.sort(standings);
                //System.out.println(standings);
            } else {
                playMatch(currentSeason, splitCount);
                count++;
            }
        }
    }

    static MatchLog playMatch(Season currentSeason, int splitCount) {
        if (!currentSeason.isFinished()) {
            Match match = currentSeason.playMatch();
            //System.out.println(match);

            Team winner = match.getMatchLog().getWinner();
            Team loser = match.getMatchLog().getLoser();

            teamToStandingMap.get(winner).wonGame();
            teamToStandingMap.get(loser).lostGame();
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
}
