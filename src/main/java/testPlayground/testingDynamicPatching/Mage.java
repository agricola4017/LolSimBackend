package testPlayground.testingDynamicPatching;

public class Mage extends Hero {

    protected static int DEFAULT_HP = 60;
    protected static int DEFAULT_ATTACK = 35;

    public Mage() {
        super(DEFAULT_HP, DEFAULT_ATTACK);
    }

    public Mage(int hp, int attack) {
        super(hp, attack);
    }

    @Override
    public HeroEnum getHeroEnum() {
        return HeroEnum.MAGE;
    }

    public int getHp() {
        return super.getHp();
    }

    public int getAttack() {
        return super.getAttack();
    }
}
