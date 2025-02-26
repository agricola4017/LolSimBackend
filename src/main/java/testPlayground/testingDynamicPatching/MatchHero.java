package testPlayground.testingDynamicPatching;

public class MatchHero {
    private int hp;
    private int attack;

    public MatchHero(int hp, int attack) {
        this.hp = hp;
        this.attack = attack;
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

    public boolean isAlive() {
        return this.hp > 0;
    }
}
