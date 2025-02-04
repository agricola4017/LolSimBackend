package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;


public class SpringSplit extends Season {
    private Deque<Match> matchesToBePlayed;
    private int oneSetCount;
    private Team winner;
    private List<Team> teams;

    private final static String name = "Spring Split";

    /**
     * TODO: consider moving all this organization to a 'start' method
     * outputs - matchesToBePlayed : schedule
     * @param teams
     */
    public SpringSplit(List<Team> teams) {
        this.oneSetCount = teams.size()/2;
        this.matchesToBePlayed = new ArrayDeque<>();
        this.teams = teams;

        /**
         * rotating algorithm for round robin
         */

        //assume teams is even and > 2, if odd incorporate bye (But skip for now)

        List<Team> teamList1 = new ArrayList<>();
        List<Team> teamList2 = new ArrayList<>();

        //break into two, 2nd list is reversed
        for (int i = 0; i < teams.size()/2; i++) {
            teamList1.add(teams.get(i));
            teamList2.add(teams.get(teams.size()-1-i));
        }

        //make vertical matches
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int j = 0; j < teamList1.size(); j++) {
                matchesToBePlayed.add(new Match(teamList1.get(j), teamList2.get(j), 1));
            }

            //rotates
            teamList1.add(1, teamList2.get(0));
            teamList2.remove(0);
            teamList2.add(teamList1.get(teamList1.size()-1));
            teamList1.remove(teamList1.size()-1);
        }

        //repeats for second round robin but with flipped seocnd list

        teamList1.clear();
        teamList2.clear();

        for (int i = 0; i < teams.size()/2; i++) {
            teamList1.add(teams.get(i));
            teamList2.add(teams.get(i + teams.size()/2));
        }

        //make vertical matches
        for (int i = 0; i < teams.size() - 1; i++) {
            for (int j = 0; j < teamList1.size(); j++) {
                matchesToBePlayed.add(new Match(teamList1.get(j), teamList2.get(j), 1));
            }

            //rotates
            teamList1.add(1, teamList2.get(0));
            teamList2.remove(0);
            teamList2.add(teamList1.get(teamList1.size()-1));
            teamList1.remove(teamList1.size()-1);
        }
    }

    public Deque<Match> getMatchesToBePlayed() {
        return matchesToBePlayed;
    }

    public void setMatchesToBePlayed(Deque<Match> matchesToBePlayed) {
        this.matchesToBePlayed = matchesToBePlayed;
    }

    @Override
    public Match playMatch() {
        Match match = matchesToBePlayed.poll();
        for (Team team : match.getTeams()) {
            team.normalizePlayers();
        }
        match.playMatch();
        return match;
    }

    public int getOneSetCount() {
        return oneSetCount;
    }

    public boolean isFinished() {
        return matchesToBePlayed != null && matchesToBePlayed.isEmpty();
    }

    public String getName() {
        return name;
    }

    public Season newInstance(List<Team> teams) {
        return new SpringSplit(teams);
    }

    public Team getWinner() {
        return winner;
    }   

    public Season generateNextSeason(List<Team> teams) {
        teams = teams.subList(0, Math.min(6, teams.size()));
        return new SpringPlayoffs(teams);
    }
}
