package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;

public class SpringPlayoffs extends Season {
    private LinkedList<Match> matchesToBePlayed;
    private int oneSetCount;
    private Set<Team> winnersBracket;
    private Set<Team> losersBracket;
    private List<Team> teams;

    private final static String name = "Spring Playoffs";

    private int seriesLength;
    private int winPoint;
    private Team[] matchTeams;
    private int[] matchTeamLosses;
    private int matchesIndex;

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
        this.winnersBracket = new HashSet<>(teams);
        this.losersBracket = new HashSet<>();
        this.teams = new ArrayList<>(teams);

        setupMatches(3);
    }

    public void setupMatches(int seriesLength) {
        //assume teams is even and > 2, if odd incorporate bye (But skip for now)
        this.seriesLength = seriesLength;
        this.winPoint = (int)Math.ceil(seriesLength/2.0);
        this.matchTeamLosses = new int[2];
        for (int i = 0; i < teams.size()/2; i++) {
            Team one = teams.get(i);
            Team two = teams.get(teams.size()-i-1);
            matchesToBePlayed.add(new Match(one, two, seriesLength));
            System.out.println("matches added: " + one.getTeamName() + " vs " + two.getTeamName());

        }
    }

    public Queue<Match> getMatchesToBePlayed() {
        return matchesToBePlayed;
    }

    public void setMatchesToBePlayed(Queue<Match> matchesToBePlayed) {
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
            System.out.println("winnersBracket: ");
            winnersBracket.stream().map(Team::getTeamName).forEach(System.out::println);
            System.out.println("losersBracket: " );
            losersBracket.stream().map(Team::getTeamName).forEach(System.out::println);
            setupMatches(5);
            return playMatch();
        } else {
            Match match = matchesToBePlayed.poll();
            match.playMatch();  
            if (matchesIndex >= seriesLength - 1 || (matchTeamLosses[0] >= winPoint - 1|| matchTeamLosses[1] >= winPoint - 1)) {
                Team loser;
                Team winner;
                if (matchTeamLosses[0] > matchTeamLosses[1]) {
                    winner = matchTeams[1];
                    loser = matchTeams[0];
                } else {
                    loser = matchTeams[1];
                    winner = matchTeams[0];
                }

                if (winnersBracket.contains(loser)) {
                    winnersBracket.remove(loser);
                    losersBracket.add(loser);
                } else {
                    losersBracket.remove(loser);
                    teams.remove(loser);
                }

                System.out.println("Winner of the Series is " + winner.getTeamName());
                matchesIndex = 0;
                matchTeamLosses = new int[matchTeams.length];
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
            }
            return match;
        }
    }

    public int getOneSetCount() {
        return oneSetCount;
    }

    public boolean isFinished() {
        return teams.size() == 1;
    }

    public String getName() {
        return name;
    }

    public Season newInstance(List<Team> teams) {
        return new SpringPlayoffs(teams);
    }
}
