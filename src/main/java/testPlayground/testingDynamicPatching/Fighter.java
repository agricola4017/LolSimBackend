package testPlayground.testingDynamicPatching;

public class Fighter extends Hero {

    protected static int DEFAULT_HP = 100;
    protected static int DEFAULT_ATTACK = 20;

    public Fighter() {
        super(DEFAULT_HP, DEFAULT_ATTACK);
    }

    public Fighter(int hp, int attack) {
        super(hp, attack);
    }

    @Override
    public HeroEnum getHeroEnum() {
        return HeroEnum.FIGHTER;
    }

    public int getHp() {
        return super.getHp();
    }

    public int getAttack() {
        return super.getAttack();
    }
}