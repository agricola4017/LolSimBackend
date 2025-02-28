package GameObjects.MatchesAndSeasons;

import GameObjects.HerosAndClasses.HeroEnum;
import GameObjects.TeamsAndPlayers.Player;

public class MatchLogPlayerStat {
    private int kills;
    private int deaths;
    private int cs;
    private int gold;
    private Player player;
    private HeroEnum hero;

    public MatchLogPlayerStat(Player player, int kills, int deaths, int cs, int gold, HeroEnum hero) {
        this.kills = kills;
        this.deaths = deaths;
        this.cs = cs;
        this.gold = gold;
        this.player = player;
        this.hero = hero;
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

    public HeroEnum getHeroEnum() {
        return hero;
    }

    @Override
    public String toString() {
        return "MatchLogPlayerStat{" +
                "player=" + player +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", cs=" + cs +
                ", gold=" + gold +
                '}';
    }
}
