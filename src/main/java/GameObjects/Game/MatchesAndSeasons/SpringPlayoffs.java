package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;

public class SpringPlayoffs extends Season {
    private Deque<Match> matchesToBePlayed;
    private int oneSetCount;
    private List<Team> winnersBracket;
    private List<Team> losersBracket;
    private List<Team> teams;

    private final static String name = "Spring Playoffs";

    private int seriesLength;
    private int winPoint;
    private Team[] matchTeams;
    private int[] matchTeamLosses;
    private int matchesIndex;

    private Team winner;

    /**
     * TODO: consider moving all this organization to a 'start' method
     * outputs - matchesToBePlayed : schedule
     * @param teams
     */

    SpringPlayoffs() {

    }

    public SpringPlayoffs(List<Team> teams) {
        this.oneSetCount = teams.size()/2;
        this.matchesToBePlayed = new LinkedList<>();
        this.winnersBracket = new ArrayList<>(teams);
        this.losersBracket = new ArrayList<>();
        this.teams = teams;
        if (teams.size() != 6) {
            System.out.println("Warning: not enough teams for playoffs. Using a subset of teams.");
        } else {
            
            setupMatches(3);
        }
        System.out.println("teams: " + teams.size());

    }

    public void setupMatches(int seriesLength) {
        //assume teams is even and > 2, if odd incorporate bye (But skip for now)
        this.seriesLength = seriesLength;
        this.winPoint = (int)Math.ceil(seriesLength/2.0);
        this.matchTeamLosses = new int[2];

        List<Team> winnersBracketList = new ArrayList<>(winnersBracket);
        List<Team> losersBracketList = new ArrayList<>(losersBracket);

        if (teams.size() != 2) { 
            
            for (int i = 0; i < winnersBracketList.size()/2; i++) {
                Team one = winnersBracketList.get(i);
                Team two = winnersBracketList.get(winnersBracketList.size()-i-1);
                matchesToBePlayed.add(new Match(one, two, seriesLength));
                //System.out.println("matches added: " + one.getTeamName() + " vs " + two.getTeamName() + " matches: " + seriesLength + " winPoint: " + winPoint);
            }

            for (int i = 0; i < losersBracketList.size()/2; i++) {
                Team one = losersBracketList.get(i);
                Team two = losersBracketList.get(losersBracketList.size()-i-1);
                matchesToBePlayed.add(new Match(one, two, seriesLength));
                //System.out.println("matches added: " + one.getTeamName() + " vs " + two.getTeamName() + " matches: " + seriesLength + " winPoint: " + winPoint);
            }
        } else {
            if (winnersBracketList.isEmpty()) {
                matchesToBePlayed.add(new Match(losersBracketList.get(0), losersBracketList.get(1), seriesLength));
            } else {
                matchesToBePlayed.add(new Match(winnersBracketList.get(0), losersBracketList.get(0), seriesLength));
            }
        }

        /* for (Match match : matchesToBePlayed) {
            for (Team team : match.getTeams()) {
                System.out.print(team.getTeamName());
            }
            System.out.println();
        } */
        //System.out.println("matchesToBePlayed size: " + matchesToBePlayed.size());
    }

    public Deque<Match> getMatchesToBePlayed() {
        return matchesToBePlayed;
    }

    public void setMatchesToBePlayed(Deque<Match> matchesToBePlayed) {
        this.matchesToBePlayed = matchesToBePlayed;
    }

    @Override
    /**
     * Plays a match (Series) from the split 
     */
    public Match playMatch() {
       // System.out.println("Playing match..." + "matchesIndex: " + matchesIndex + " seriesLength: " + seriesLength + " winPoint: " + winPoint); 
        //System.out.println("matchTeamLosses : " + matchTeamLosses[0] + " " + matchTeamLosses[1]);

        if (matchesToBePlayed.isEmpty()) {
            this.matchesIndex = 0;
            
            //setupMatches(5);
            //return playMatch();
            setupMatches(3);

            return playMatch();
        } else {
            //System.out.println("matchesIndex " + matchesIndex + " seriesLength " + seriesLength + " winPoint " + winPoint);
            Match match = matchesToBePlayed.poll();
            match.playMatch();  
            Boolean aboutToHitMaxGames = matchesIndex >= seriesLength - 1;
            Boolean team0Lost = matchTeamLosses[0] >= winPoint - 1 && match.getMatchLog().getLoser() == matchTeams[0];
            Boolean team1Lost = matchTeamLosses[1] >= winPoint - 1 && match.getMatchLog().getLoser() == matchTeams[1];  
            //System.out.println(match.getMatchLog().getWinner().getTeamName() + " beat " + match.getMatchLog().getLoser().getTeamName());
            if (aboutToHitMaxGames || team0Lost || team1Lost) {
                Team loser;
                Team winner;
                if (team0Lost) {
                    loser = matchTeams[0];
                    winner = matchTeams[1];
                } else if (team1Lost){
                    loser = matchTeams[1];
                    winner = matchTeams[0];
                } else {
                    if (matchTeamLosses[1] > matchTeamLosses[0]) {
                        loser = matchTeams[1];
                        winner = matchTeams[0];
                    } else {
                        loser = matchTeams[0];
                        winner = matchTeams[1];
                    }
                }

                if (winnersBracket.contains(loser)) {
                    winnersBracket.remove(loser);
                    losersBracket.add(loser);
                } else {
                    losersBracket.remove(loser); 
                    teams.remove(loser);
                }

                /* System.out.println("Winner of the Series is " + winner.getTeamName());

                System.out.print("Winners: ");
                winnersBracket.forEach(team -> System.out.print(team.getTeamName()));
                System.out.println();
                System.out.print("Losers: ");
                losersBracket.forEach(team -> System.out.print(team.getTeamName()));
                System.out.println(); */
                matchesIndex = 0;
            } else {
                if (matchesIndex == 0) {
                    seriesLength = match.getMatches();
                    winPoint = (int)Math.ceil(seriesLength/2.0);
                    matchTeams = match.getTeams();
                    matchTeamLosses = new int[matchTeams.length];
                }
                
                matchesIndex++;

                Team loser = match.getMatchLog().getLoser();

                if (loser == matchTeams[0]) {
                    matchTeamLosses[0]++;
                } else if (loser == matchTeams[1]) {
                    matchTeamLosses[1]++;
                }
                matchesToBePlayed.addFirst(new Match(matchTeams[0], matchTeams[1], seriesLength));
            }
            return match;
        }
    }

    public int getOneSetCount() {
        return oneSetCount;
    }

    public boolean isFinished() {
        Boolean isFinished = teams.size() == 1;
        //System.out.println("isFinished: " + isFinished);
        if (isFinished) {
            //System.out.println("Winner of Playoffs is: " + winner.getTeamName());
            winner = teams.get(0);
        }
        return isFinished;
    }

    public String getName() {
        return name;
    }

    public Season newInstance(List<Team> teams) {
        return new SpringPlayoffs(teams);
    }

    public Team getWinner() {
        return winner;
    }

    public Season generateNextSeason(List<Team> teams) {
        return new SpringSplit(teams);
    }
}
