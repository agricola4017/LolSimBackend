package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Team;

import java.util.*;

public class Game {
    static boolean gameIsRunning = true;
    static Queue<Season> seasonsToPlay = new LinkedList<>();
    static List<Team> teams;
    static Map<Integer, Team> teamMap;
    static List<Standing> standings;
    static List<Standing> oldStandings;
    static Map<Integer, Standing> teamToStandingMap;

    //win,loss, teamid
    // after update, local sort
    // h2h records
    // kda, etc...

    //self sorting access by ID, but also traverse
    // sort is local
    //stores teamid -> win, loss
    static List<Player> players;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Game is starting");

        /**
         * load players and teams
         */

        players = new ArrayList<>();
        teams = new ArrayList<>();
        teamMap = new HashMap<Integer, Team>();
        standings = new ArrayList<>();
        teamToStandingMap = new HashMap<>();

        //sql calls to load
        testTeams();
        testPlayers();

        //testSignPlayers();
        //Season freeAgency = new freeAgency();
        Season springSplit = new SpringSplit(new ArrayList<>(teamMap.keySet()));
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

            seasonsToPlay.add(springSplit.newInstance(new ArrayList<>(teamMap.keySet())));

            /*for (Match a: currentSeason.getMatchesToBePlayed()) {
                System.out.println(a);
            }*/

            int count = 1;
            while (!currentSeason.isFinished()) {
                //pause thread until something comes
                Thread.sleep(50);
                if (count > currentSeason.getOneSetCount()) {
                    count = 1;
                    //balance teams
                    //.out.println();
                    Collections.sort(standings);
                    System.out.println(standings);
//                    System.out.println(match);
                } else {
                    Match match = currentSeason.playMatch();
                    //System.out.print(match + " ");

                    int winner = match.getWinner();
                    int loser = match.getLoser();

                    teamToStandingMap.get(winner).wonGame();
                    teamToStandingMap.get(loser).lostGame();

                    count++;
                }
            }
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

        Team team1 = new Team(0, "Team1");
        Team team2 = new Team(1, "Team2");
        Team team3 = new Team(2, "Team3");
        Team team4 = new Team(3, "Team4");
        Team team5 = new Team(4, "Team5");
        Team team6 = new Team(5, "Team6");
        Team team7 = new Team(6, "Team7");
        Team team8= new Team(7, "Team8");
        Team team9 = new Team(8, "Team9");
        Team team10 = new Team(9, "Team10");
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
                players.add(player);
                teamMap.get(i).addPlayer(player);
            }
        }
    }

    static void initStandings() {
        for (int i = 0; i < teams.size(); i++) {
            Standing standing = new Standing(teams.get(i).getTeamID());
            standings.add(standing);
            teamToStandingMap.put(teams.get(i).getTeamID(), standing);
        }
    }
}
