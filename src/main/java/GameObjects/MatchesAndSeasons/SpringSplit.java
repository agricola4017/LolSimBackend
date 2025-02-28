package GameObjects.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Standing;
import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import GameObjects.HerosAndClasses.HeroFactory;

/**
 * Basic Spring Split class
 * Round Robin
 * top 6 qualify to Playoffs
 */
public class SpringSplit extends Season {
    private final static String name = "Spring Split";

    public SpringSplit(List<Team> teams) {
        super(teams, name);

        setupMatches();
    }

    public SpringSplit(List<Team> teams, List<Standing> oldStandings) {
        super(teams, oldStandings, name);

        setupMatches();
    }

    /**
     * Sets up the matches for the season
     * Rotating algorithm for Double round robin
     */
    public void setupMatches() {    
        /**
         * rotating algorithm for round robin
         */

        //assume teams is even and > 2, if odd incorporate bye (But skip for now)
        List<Team> teamList1 = new ArrayList<>();
        List<Team> teamList2 = new ArrayList<>();

        //break into two, 2nd list is reversed
        for (int i = 0; i < super.getTeams().size()/2; i++) {
            teamList1.add(super.getTeams().get(i));
            teamList2.add(super.getTeams().get(super.getTeams().size()-1-i));
        }

        //make vertical matches
        for (int i = 0; i < super.getTeams().size() - 1; i++) {
            for (int j = 0; j < teamList1.size(); j++) {
                Team[] teams = {teamList1.get(j), teamList2.get(j)};
                super.addSeries(new Series(teams, 1));
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

        for (int i = 0; i < super.getTeams().size()/2; i++) {
            teamList1.add(super.getTeams().get(i));
            teamList2.add(super.getTeams().get(i + super.getTeams().size()/2));
        }

        //make vertical matches
        for (int i = 0; i < super.getTeams().size() - 1; i++) {
            for (int j = 0; j < teamList1.size(); j++) {
                super.addSeries(new Series(new Team[] {teamList1.get(j), teamList2.get(j)}, 1));
            }

            //rotates
            teamList1.add(1, teamList2.get(0));
            teamList2.remove(0);
            teamList2.add(teamList1.get(teamList1.size()-1));
            teamList1.remove(teamList1.size()-1);
        }    
    }

    public boolean isFinished() {
        Boolean isFinished = super.isSeriesToBePlayedEmptyAndNotNull();
        if (isFinished) {
            super.setWinner(super.getStandings().get(0).getTeam());
            super.setRunnerUp(super.getStandings().get(1).getTeam());
        }
        return isFinished;
    } 

    public Season generateNextSeason(List<Team> teams) {
        //teams = teams.subList(0, Math.min(6, teams.size()));
        List<Team> sublist = new ArrayList<>();
        for (int i = 0; i < teams.size() && i < 6; i++) {
            sublist.add(teams.get(i));
        }
        return new SpringPlayoffs(sublist, super.getStandings());
    }

    @Override
    public String toString() {
        String ret = "";
        
        int j = 1;
        for (int i = 0; i < super.getStandings().size(); i++) {
            Standing standing = super.getStandings().get(i);
            String standingOutput = j + ". " + standing + " | (OVR:" + standing.getTeam().getPlayerRoster().getOVR() + ")" + " | Prev. ";
            if (super.getOldStandings() != null && !super.getOldStandings().isEmpty() && i < super.getOldStandings().size()) {
                standingOutput += super.getOldStandings().get(i);
            } else {
                standingOutput += "0-0";
            }
            ret += standingOutput + " | TID:" + standing.getTeam().getTeamID() + "\n";
            j++;
        }
        return ret;
    }
}
