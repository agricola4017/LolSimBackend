package GameObjects.Game.MatchesAndSeasons;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;
import GameObjects.TeamsAndPlayers.Team;

import java.util.Map;

public class MatchLogTeamStat {
    private int kills;
    private int deaths;
    private int cs;
    private int gold;
    private Team team;
    private Map<Player, MatchLogPlayerStat> playerToPlayerStatMap;
    private final Map<Position, Player> positionToPlayerMap;

    public MatchLogTeamStat(Team team, int kills, int deaths, int cs, int gold, Map<Player, MatchLogPlayerStat> playerToPlayerStatMap, Map<Position, Player> positionToPlayerMap) {
        this.team = team;
        this.kills = kills;
        this.deaths = deaths;
        this.cs = cs;
        this.gold = gold;
        this.playerToPlayerStatMap = playerToPlayerStatMap;
        this.positionToPlayerMap = positionToPlayerMap;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Map<Player, MatchLogPlayerStat> getPlayerToPlayerStatMap() {
        return playerToPlayerStatMap;
    }

    public void setPlayerToPlayerStatMap(Map<Player, MatchLogPlayerStat> playerToPlayerStatMap) {
        this.playerToPlayerStatMap = playerToPlayerStatMap;
    }

    public void addGold(int gold) {
        this.gold += gold;
    }

    public void addCs(int cs) {
        this.cs += cs;
    }

    public void addKill(int kill) {
        this.kills+=kill;
    }

    public void addDeath(int death) {
        this.deaths+=death;
    }

    public Map<Position, Player> getPositionToPlayerMap() {
        return positionToPlayerMap;
    }

    /*@Override
    public String toString() {
        return "MatchLogTeamStat{" +
                "playerToPlayerStatMap=" + playerToPlayerStatMap +
                '}';
    }*/

    @Override
    public String toString() {
        String ret = "";
        
        ret += team.getTeamName() + "\n";
        ret += "Position | KDA | CS | GOLD | OVR\n";
        for (Map.Entry<Position, Player> positionToPlayer : positionToPlayerMap.entrySet()) {
            Player player = positionToPlayer.getValue();
            Position position = positionToPlayer.getKey();
            MatchLogPlayerStat matchLogPlayerStat = playerToPlayerStatMap.get(player);
            ret += position + " | " + player.getPlayerName() + " | ";
            ret += matchLogPlayerStat.getKills() + "/" + matchLogPlayerStat.getDeaths() + " | ";
            ret +=  matchLogPlayerStat.getCs()+ " | " + matchLogPlayerStat.getGold();
            ret += " | " + player.getStat().getOVR() + "/" + player.getStat().getPotential();
            ret += "\n";
        }
        return ret;
    }
}
