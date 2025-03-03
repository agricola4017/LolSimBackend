package GameObjects.MatchesAndSeasons;

import GameObjects.HerosAndClasses.ClassEnum;
import GameObjects.HerosAndClasses.HeroEnum;
import GameObjects.HerosAndClasses.Hero;

public class MatchHero {
    private int hp;
    private int maxHP;
    private int attack;
    private ClassEnum type;
    private HeroEnum heroEnum;
    private int team;

    public MatchHero(int hp, int attack, HeroEnum heroEnum, ClassEnum type,int team) {
        this.hp = hp;
        this.maxHP = hp;
        this.attack = attack;
        this.type = type;
        this.heroEnum = heroEnum;
        this.team = team;
    }

    public int getTeam() {
        return team;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public void gotAttacked(int damage) {
        this.hp -= damage;
    }

    public ClassEnum getType() {
        return type;
    }

    public HeroEnum getHeroEnum() {
        return heroEnum;
    }

    public boolean isAlive() {
        return this.hp > 0;
    }

    public int getMaxHP() {
        return maxHP;
    }
    
}
