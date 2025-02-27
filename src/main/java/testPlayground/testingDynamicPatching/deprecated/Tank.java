package testPlayground.testingDynamicPatching;

public class Tank extends Hero {

    protected static int DEFAULT_HP = 250;
    protected static int DEFAULT_ATTACK = 8;

    public Tank() {
        super(DEFAULT_HP, DEFAULT_ATTACK);
    }

    public Tank(int hp, int attack) {
        super(hp, attack);
    }

    @Override
    public HeroEnum getHeroEnum() {
        return HeroEnum.TANK;
    }

    public int getHp() {
        return super.getHp();
    }

    public int getAttack() {
        return super.getAttack();
    }
}
