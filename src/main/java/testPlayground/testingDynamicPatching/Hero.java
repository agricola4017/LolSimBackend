package testPlayground.testingDynamicPatching;

public abstract class Hero {

    private int hp;
    private int attack;

    public Hero(int hp, int attack) {
        this.hp = hp;
        this.attack = attack;
    }

    public abstract HeroEnum getHeroEnum();

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int setAttack(int attack) {
        this.attack = attack;
        return attack;
    }

    public int setHp(int hp) {
        this.hp = hp;
        return hp;
    }

    public MatchHero generateMatchHero() {
        return new MatchHero(this.hp, this.attack);
    }
}
