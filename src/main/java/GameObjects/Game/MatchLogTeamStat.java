package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;
import GameObjects.TeamsAndPlayers.Team;

import java.util.Map;

public class MatchLogTeamStat {
    private int kills;
    private int deaths;
    private int cs;
    private int gold;
    private Map<Player, MatchLogPlayerStat> playerToPlayerStatMap;

    public MatchLogTeamStat(int kills, int deaths, int cs, int gold, Map<Player, MatchLogPlayerStat> playerToPlayerStatMap) {
        this.kills = kills;
        this.deaths = deaths;
        this.cs = cs;
        this.gold = gold;
        this.playerToPlayerStatMap = playerToPlayerStatMap;
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

    /*@Override
    public String toString() {
        return "MatchLogTeamStat{" +
                "playerToPlayerStatMap=" + playerToPlayerStatMap +
                '}';
    }*/

    @Override
    public String toString() {
        String ret = "";
        int posIter = 0;
        for (Map.Entry<Player,MatchLogPlayerStat> playerToStat : playerToPlayerStatMap.entrySet()) {
            Player player = playerToStat.getKey();
            MatchLogPlayerStat matchLogPlayerStat = playerToStat.getValue();
            ret += Position.values()[posIter] + " - " + player.getPlayerName() + " | ";
            ret += "KDA: " + matchLogPlayerStat.getKills() + "/" + matchLogPlayerStat.getDeaths() + " ";
            ret += "CS: " + matchLogPlayerStat.getCs() + " " + "Gold: " + matchLogPlayerStat.getGold();
            ret += "\n";
            posIter++;
        }
        return ret;
    }
}
