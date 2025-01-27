package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;
import com.sun.source.tree.Tree;

import java.util.*;

public class Game {
    static boolean gameIsRunning = true;
    static Queue<Season> seasonsToPlay = new LinkedList<>();
    static List<Team> teams;
    static Map<Integer, Team> teamMap;
    static List<Standing> standings;
    static List<Standing> oldStandings;
    static Map<Team, Standing> teamToStandingMap;
    static List<Player> activePlayers;;  
    static Scanner scanner = new Scanner(System.in);
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

     static void awaitPlayerPrompt(int splitCount, Season currentSeason) throws InterruptedException {
    
        System.out.println("Options");
        System.out.println("--------------------------------------");
         System.out.println("1: play season\n2: play game\n3: see team info\n4: see standings");
        System.out.println("Enter command: ");
        String input = scanner.nextLine();

        switch (input) {
            case "1":
            case "play season":
                playSeason(currentSeason, splitCount);
                break;
            case "2":
            case "play game":
                MatchLog matchLog = playMatch(currentSeason, splitCount);
                if (matchLog != null) {
                    System.out.println("Match is finished");
                    System.out.println(matchLog.toString());
                } else {
                    System.out.println("Season is finished");
                }
                break;
            case "3":
            case "see team info":
                System.out.println(teams.get(0).getPlayerRoster().toString());
                System.out.println(teamToStandingMap.get(teams.get(0)));
                break;
            case "4":
            case "see standings":
                Collections.sort(standings);
                printStandings();
                break;
            case "5": 
                System.out.println(teams.get(0).getPlayers());
                break;
            default:
                break;
        }
    }

    static void playGame() throws InterruptedException{
        int splitCount = 0;
        while(gameIsRunning) {

            //initialize standings
            initStandings();

            Season currentSeason = seasonsToPlay.poll();
            System.out.println(currentSeason.getName() + " " + splitCount);

            while (!currentSeason.isFinished()) {
                awaitPlayerPrompt(splitCount, currentSeason);
            }

            Collections.sort(standings);

            printStandings();

            cleanupLeague();

            splitCount++;
            gameIsRunning = !seasonsToPlay.isEmpty();
        }
    }

    static void printStandings() {
        System.out.println("Final Standings");

        int j = 1;
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            String standingOutput = j + ". " + standing + " (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " , Prev. Seasons ";
            if (oldStandings != null) {
                standingOutput += oldStandings.get(i);
            } else {
                standingOutput += "0-0";
            }
            System.out.println(standingOutput);
            j++;
        }
        System.out.println("OVR Standings");
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
                System.out.println(i + ". " + team.getTeamName() + " " + entry.getKey());
                i++;
            }
        }

        System.out.println("Winner is " + standings.get(0));
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

    static void playSeason(Season currentSeason, int splitCount) throws InterruptedException {

        int count = 1;
        while (!currentSeason.isFinished()) {
            for (Team t : teams) {
                t.getPlayerRoster().normalizePlayers(t.getPlayers());
            }
            //pause thread until something comes
            Thread.sleep(10);

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
            teamMap.get(i).getPlayerRoster().normalizePlayers(teamMap.get(i).getPlayers());
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
