package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import GameObjects.TeamsAndPlayers.Standing;

public class SpringPlayoffs extends Season {
    //Brackets and team tracker for Season with Elimination
    private List<Team> winnersBracket;
    private List<Team> losersBracket;
    private Set<Team> playingTeams;

    private final static int ELIMINATION_SERIES_LENGTH = 5;
    private final static int REGULAR_SERIES_LENGTH = 3;

    private final static String name = "Spring Playoffs";

    public SpringPlayoffs(List<Team> teams) {
        super(teams, name);
        playingTeams = new HashSet<>(teams);

        
        this.winnersBracket = new ArrayList<>(this.playingTeams);
        this.losersBracket = new ArrayList<>();
        setupMatches();
    }

    public SpringPlayoffs(List<Team> teams, List<Standing> oldStandings) {
        super(teams, oldStandings, name);
        playingTeams = new HashSet<>(teams);
        
        this.winnersBracket = new ArrayList<>(this.playingTeams);
        this.losersBracket = new ArrayList<>();
        setupMatches();
    }

    /**
     * Sets up the matches for the season
     * 
     * Goes through the winners bracket and losers bracket and creates matches inside each bracket
     * If a match cannot be created due to an odd number of teams, a bye will be created (NOT YET IMPLEMENTED)
     * if only two teams remain, a grand finals match will be created
     */
    private void setupMatches() {
        //assume teams is even and > 2, if odd incorporate bye (But skip for now)

        if (this.playingTeams.size() != 2) { 
            
            for (int i = 0; i < winnersBracket.size()/2; i++) {
                Team one = winnersBracket.get(i);
                Team two = winnersBracket.get(winnersBracket.size()-i-1);
                super.addSeries(new Series(new Team[] {one, two}, REGULAR_SERIES_LENGTH));
                //System.out.println("matches added: " + one.getTeamName() + " vs " + two.getTeamName() + " matches: " + seriesLength + " winPoint: " + winPoint);
            }

            for (int i = 0; i < losersBracket.size()/2; i++) {
                Team one = losersBracket.get(i);
                Team two = losersBracket.get(losersBracket.size()-i-1);
                super.addSeries(new Series(new Team[] {one, two}, ELIMINATION_SERIES_LENGTH));
                //System.out.println("matches added: " + one.getTeamName() + " vs " + two.getTeamName() + " matches: " + seriesLength + " winPoint: " + winPoint);
            }
        } else {
            //Matches the last game of the seasons, grand finals 
            if (winnersBracket.isEmpty()) {
                super.addSeries(new Series(new Team[] {losersBracket.get(0), losersBracket.get(1)}, ELIMINATION_SERIES_LENGTH));
            } else {
                super.addSeries(new Series(new Team[] {winnersBracket.get(0), losersBracket.get(0)}, ELIMINATION_SERIES_LENGTH));
            }
        }
    }

    /**
     * Plays a match (Series) from the split 
     * Assumes there are matches to be played
     */
    @Override
    public Match playMatch() {
       // System.out.println("Playing match..." + "matchesIndex: " + matchesIndex + " seriesLength: " + seriesLength + " winPoint: " + winPoint); 
        //System.out.println("matchTeamLosses : " + matchTeamLosses[0] + " " + matchTeamLosses[1]);

        Series series = super.peekSeriesToBePlayed();
        Match match = series.playMatch();
        for (Team team : match.getTeams()) {
            team.normalizePlayers();
        }

        if (series.isFinished()) {
            Team loser = series.getLoser();

            if (this.winnersBracket.contains(loser)) {
                this.winnersBracket.remove(loser);
                this.losersBracket.add(loser);
            } else {
                this.losersBracket.remove(loser); 
                this.playingTeams.remove(loser);
            }

            super.updateStandings(series.getWinner(), loser);

            super.pollSeriesToBePlayed();
        }

        if (super.isSeriesToBePlayedEmptyAndNotNull()) {
            setupMatches();
        }

        return match;
     }

    public boolean isFinished() {
        Boolean isFinished = this.playingTeams.size() == 1;
        //System.out.println("isFinished: " + isFinished);
        if (isFinished) {
            //System.out.println("Winner of Playoffs is: " + winner.getTeamName());
            super.setWinner((Team)this.playingTeams.toArray()[0]);
            super.setRunnerUp(super.getStandings().get(1).getTeam());
        }
        return isFinished;
    }

    public Season generateNextSeason(List<Team> teams) {
        return new SpringSplit(teams, super.getStandings());
    }

    @Override
    /**
     * Should implement playoffs custom string
     */
    public String toString() {
        String ret = super.toString();
        ret += "\n";

        ret+= "Winners Bracket: \n";
        for (Team team: winnersBracket) {
            ret += team.getTeamName() + "\n";
        }

        ret+= "Losers Bracket: \n";
        for (Team team: losersBracket) {
            ret += team.getTeamName() + "\n";
        }
        return ret;
    }
}
