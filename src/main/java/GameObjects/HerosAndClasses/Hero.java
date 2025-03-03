package GameObjects.HerosAndClasses;

import GameObjects.MatchesAndSeasons.MatchHero;

public class Hero {

    public HeroEnum heroEnum;
    public ClassEnum classEnum;
    int hp;
    int attack;

    protected Hero(HeroEnum heroEnum) {
        this.heroEnum = heroEnum;
        this.classEnum = heroEnum.getClassEnum();
        this.hp = classEnum.getDefaultHP();
        this.attack = classEnum.getDefaultAttack();
    }

    public int getHp() {
        return this.hp;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefaultHP() {
        return classEnum.getDefaultHP();
    }

    public int getDefaultAttack() {
        return classEnum.getDefaultAttack();
    }

    public ClassEnum getClassEnum() {
        return classEnum;
    }

    public HeroEnum getHeroEnum() {
        return heroEnum;
    }

    public MatchHero generateMatchHero(int team) {
        return new MatchHero(this.getHp(), this.getAttack(), this.getHeroEnum(), this.getClassEnum(), team);
    }
}
