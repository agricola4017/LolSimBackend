package testPlayground.testingDynamicPatching;

public enum HeroEnum {
    FIGHTER(Fighter.class),
    MAGE(Mage.class),
    TANK(Tank.class);

    private final Class<? extends Hero> heroClass;

    HeroEnum(Class<? extends Hero> heroClass) {
        this.heroClass = heroClass;
    }

    public static HeroEnum[] getHeroEnums() {
        return values();
    }

    public Class<? extends Hero> getHeroClass() {
        return heroClass;
    }

    public HeroEnum classifyHeroEnum(Hero hero) {
        for (HeroEnum heroEnum : values()) {
            if (heroEnum.getHeroClass().isInstance(hero)) {
                return heroEnum;
            }
        }
        return null;
    }
}
