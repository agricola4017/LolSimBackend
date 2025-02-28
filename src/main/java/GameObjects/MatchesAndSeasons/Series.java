package GameObjects.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Team;
import java.util.Queue;
import java.util.ArrayDeque;
import java.io.Serializable;

/**
 * Defines a series of matches
 */
public class Series implements Serializable {
        private final int seriesLength;
        private final int winPoint;
        private final Team[] matchTeams;
        private final int[] matchTeamLosses;
        private int matchesIndex;

        private final Queue<Match> matches;

        public Series(Team[] matchTeams, int seriesLength) {
            this.matchTeams = matchTeams;
            this.seriesLength = seriesLength;
            
            this.winPoint = (int)Math.ceil(seriesLength/2.0);
            this.matchTeamLosses = new int[2];
            this.matches = new ArrayDeque<>();

            
            this.matchesIndex = 0;
            
            for (int i = 0; i < seriesLength; i++) {
                this.matches.add(new Match(this.matchTeams[0], this.matchTeams[1]));
            }
            
        }

        public Boolean isFinished() {
            return this.matchesIndex >= this.seriesLength || this.matchTeamLosses[0] >= this.winPoint || this.matchTeamLosses[1] >= this.winPoint;
        }

        public Match playMatch() {
            Match match = matches.poll();
            for (Team team : match.getTeams()) {
                team.normalizePlayers();
            }
            match.playMatch();;
            for (int i = 0; i < matchTeams.length; i++) {
                if (match.getMatchLog().getLoser() ==matchTeams[i]) {
                    matchTeamLosses[i]++;
                }
            }
            this.matchesIndex++;
            return match;
        }

        public Match playSimulatedMatch() {
            Match match = matches.poll();
            for (Team team : match.getTeams()) {
                team.normalizePlayers();
            }
            match.playSimulatedMatch();
            for (int i = 0; i < matchTeams.length; i++) {
                if (match.getMatchLog().getLoser() ==matchTeams[i]) {
                    matchTeamLosses[i]++;
                }
            }
            this.matchesIndex++;
            return match;
        }

        /**
         * Assumes the series is finished
         */
        public Team getWinner() {
            if (matchTeamLosses[0] >= winPoint) {
                return matchTeams[1];
            } else {
                return matchTeams[0];
            }
        }

        public Team getLoser() {
            if (matchTeamLosses[0] >= winPoint) {
                return matchTeams[0];
            } else {
                return matchTeams[1];
            }
        }

        public boolean isTeamInSeries(Team team) {
            return team.equals(matchTeams[0]) || team.equals(matchTeams[1]);
        }

}
