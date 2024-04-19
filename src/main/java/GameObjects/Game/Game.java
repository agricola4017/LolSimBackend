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
    static List<Player> activePlayers;

    //win,loss, teamid
    // after update, local sort
    // h2h records
    // kda, etc...

    //self sorting access by ID, but also traverse
    // sort is local
    //stores teamid -> win, loss


    public static void main(String[] args) throws InterruptedException {
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

        while(gameIsRunning) {
            //initialize standings
            initStandings();
            Season currentSeason = seasonsToPlay.poll();
            System.out.println(currentSeason.getName());

            //+seasonsToPlay.add(springSplit.newInstance(new ArrayList<>(teams)));

            /*for (Match a: currentSeason.getMatchesToBePlayed()) {
                System.out.println(a);
            }*/

            int count = 1;
            while (!currentSeason.isFinished()) {
                for (Team t: teams) {
                    t.getPlayerRoster().normalizePlayers(t.getPlayers());
                }
                //pause thread until something comes
                Thread.sleep(50);
                if (count > currentSeason.getOneSetCount()) {
                    count = 1;
                    //balance teams
                    Collections.sort(standings);
                    System.out.println(standings);
                } else {
                    Match match = currentSeason.playMatch();
                    System.out.println(match);

                    Team winner = match.getMatchLog().getWinner();
                    Team loser = match.getMatchLog().getLoser();

                    teamToStandingMap.get(winner).wonGame();
                    teamToStandingMap.get(loser).lostGame();

                    count++;
                }
            }
            Collections.sort(standings);
            System.out.println("Final Standings");
            int j = 1;
            for (Standing standing: standings) {
                System.out.println(j + ". " + standing);
                j++;
            }
            System.out.println("OVR Standings");
            SortedMap<Integer, List<Team>> ovrToTeamMap = new TreeMap<>(Collections.reverseOrder());
            for (Team team: teams) {
                if (ovrToTeamMap.containsKey(team.getPlayerRoster().getOVR())) {
                    ovrToTeamMap.get(team.getPlayerRoster().getOVR()).add(team);
                } else {
                    List<Team> teamlistInMap = new ArrayList<>();
                    teamlistInMap.add(team);
                    ovrToTeamMap.put(team.getPlayerRoster().getOVR(), teamlistInMap);
                }
            }

            int i = 1;
            for (SortedMap.Entry<Integer,  List<Team>> entry: ovrToTeamMap.entrySet()) {
                for (Team team: entry.getValue()) {
                    System.out.println(i + ". " + team.getTeamName() + " " + entry.getKey());
                    i++;
                }
            }

            System.out.println("Winner is " + standings.get(0));
            oldStandings = standings;
            standings = new ArrayList<>();
            teamToStandingMap = new HashMap<>();

            //remember old teamToStanding as well?

            gameIsRunning = !seasonsToPlay.isEmpty();
        }
        //What does a game need
        //load state, save state

        //assume no load

        //generate free agents
        //generate teams

        //cycle seasons

        //
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

    static void testPlayers() {
        for (int i = 0; i < teams.size(); i++) {
            for (int j = 0; j < 5; j++) {
                Player player = Player.generatePlayerWithTeam(i);
                activePlayers.add(player);
                teamMap.get(i).addPlayer(player);
            }
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
