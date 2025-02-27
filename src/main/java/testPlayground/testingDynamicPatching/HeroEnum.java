package testPlayground.testingDynamicPatching;

import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum HeroEnum {
    FighterA(ClassEnum.FIGHTER),
    MageA(ClassEnum.MAGE),
    TankA(ClassEnum.TANK),
    FighterB(ClassEnum.FIGHTER),
    MageB(ClassEnum.MAGE),
    TankB(ClassEnum.TANK),
    FighterC(ClassEnum.FIGHTER),
    MageC(ClassEnum.MAGE),
    TankC(ClassEnum.TANK),
    FighterD(ClassEnum.FIGHTER),
    MageD(ClassEnum.MAGE),
    TankD(ClassEnum.TANK),
    FighterE(ClassEnum.FIGHTER),
    MageE(ClassEnum.MAGE),
    TankE(ClassEnum.TANK);

    private final ClassEnum classEnum;
    private static Set<HeroEnum> availableHeroes = new HashSet<>(Arrays.asList(values()));

    HeroEnum(ClassEnum classEnum) {
        this.classEnum = classEnum;
    }

    public static HeroEnum[] getHeroEnums() {
        return values();
    }

    public ClassEnum getClassEnum() {
        return classEnum;
    }

    public static HeroEnum getRandomHeroEnum() {
        Random random = new Random();
        HeroEnum randomHeroEnum = values()[random.nextInt(values().length)];
        availableHeroes.remove(randomHeroEnum);
        return randomHeroEnum;
    }

    public static HeroEnum getRandomHeroEnum(ClassEnum classEnum) {
        Random random = new Random();
        List<HeroEnum> filtered = availableHeroes.stream().filter(he -> he.getClassEnum() == classEnum).collect(Collectors.toList());
        if (filtered.isEmpty()) {
            return null;
        }
        HeroEnum randomHeroEnum = filtered.get(random.nextInt(filtered.size()));
        availableHeroes.remove(randomHeroEnum);
        return randomHeroEnum;
    }

    public static void resetAvailableHeroes() {
        availableHeroes = new HashSet<>(Arrays.asList(values()));
    }
}
