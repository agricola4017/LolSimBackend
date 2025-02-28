package testPlayground.testingDynamicPatching;

public class MatchHero {
    private int hp;
    private int maxHP;
    private int attack;
    private ClassEnum type;
    private int team;

    public MatchHero(int hp, int attack, ClassEnum type, int team) {
        this.hp = hp;
        this.maxHP = hp;
        this.attack = attack;
        this.type = type;
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

    public boolean isAlive() {
        return this.hp > 0;
    }

    public int getMaxHP() {
        return maxHP;
    }
    
}
