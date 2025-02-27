package testPlayground.testingDynamicPatching;

public class Fighter extends Hero {

    protected static int DEFAULT_HP = 100;
    protected static int DEFAULT_ATTACK = 20;

    public Fighter() {
        super(HeroEnum.FIGHTER);
    }
}