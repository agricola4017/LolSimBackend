package GameObjects.HerosAndClasses;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HeroFactory {

    private final static double DEFAULT_LEVER_FACTOR = 1;
    private static Map<HeroEnum, Hero> createdHeroes = new HashMap<>();
    private static Map<HeroEnum, HeroStatsTrackers> heroStatsTrackers = new HashMap<>();
    private static Map<ClassEnum, HeroStatsTrackers> classStatsTrackers = new HashMap<>();

    private static Map<ClassEnum, Double> classWinrates = new HashMap<>();
    private static Map<ClassEnum, Integer> classPlayrates = new HashMap<>();

    static {
        for (ClassEnum classEnum : ClassEnum.values()) {
            classStatsTrackers.put(classEnum, new HeroStatsTrackers(0, 0));
        }
    }

    public static Hero createHero() {
        HeroEnum heroEnum = HeroEnum.getRandomHeroEnum();
        if (heroEnum == null) {
            throw new RuntimeException("Unable to create hero");
        }
        return createHero(heroEnum);
    }

    public static Hero createHero(ClassEnum classEnum) {
        HeroEnum heroEnum = HeroEnum.getRandomHeroEnum(classEnum);
        if (heroEnum == null) {
            heroEnum = HeroEnum.getRandomHeroEnum();
            if (heroEnum == null) {
                throw new RuntimeException("Unable to create hero");
            }
        }
        return createHero(heroEnum);
    }

    public static Hero createHero(HeroEnum heroEnum) {
        if (createdHeroes.containsKey(heroEnum)) {
            return createdHeroes.get(heroEnum);
        }
        Hero hero = new Hero(heroEnum);
        createdHeroes.put(heroEnum, hero);
        heroStatsTrackers.put(heroEnum, new HeroStatsTrackers(0, 0));
        return hero;
    }

    public static void balanceLevers(ClassEnum classEnum, double current, double target, int sampleSize) {

        classPlayrates.compute(classEnum, (key, value) -> (value == null) ? 0 : value + 1);
        classWinrates.compute(classEnum, (key, value) -> (value == null) ? 0 : value + current);

        int attack = classEnum.getAttack();
        int hp = classEnum.getHP();
        int balancingHP = classEnum.getDefaultHP();
        int balancingAttack = classEnum.getDefaultAttack();
        
        //System.out.println("current: " + current + " target: " + target + " sampleSize: " + sampleSize);

        double sampleSizeFactoredLeverFactor = (1000.0/sampleSize)*DEFAULT_LEVER_FACTOR;

        Random random = new Random();
        int totalstats = balancingAttack + balancingHP;
        //System.out.println("totalstats: " + totalstats);
        double hpratio = (double)balancingHP/totalstats;
        double randomHpFactor = random.nextGaussian(hpratio, hpratio/5);
        double volatilityFactor = 1 + random.nextGaussian(1, 2);
        double growthFactor = volatilityFactor*(target-current)/(sampleSizeFactoredLeverFactor);
        double randomAttackFactor = 1 - randomHpFactor;
        //System.out.println("buff = " + (target > current));
        //System.out.println(growthFactor);
        //System.out.println(hp*randomHpFactor + " " + (target - current) + " " + DEFAULT_LEVER_FACTOR);
        int newHpDelta = (int)((totalstats) * randomHpFactor * (growthFactor));
        //System.out.println(hp*randomHpFactor);
        int newAttackDelta = (int)((totalstats) * randomAttackFactor * (growthFactor));
        //System.out.println(newHpDelta + " " + newAttackDelta);
        hp = balancingHP + newHpDelta;
        hp = Math.max(hp, 1);
        attack = balancingAttack + newAttackDelta;
        attack = Math.max(attack, 1);

        classEnum.setHP(hp);
        classEnum.setAttack(attack);
    }

    public static void resetStatsTrackers() {
        heroStatsTrackers.values().forEach(HeroStatsTrackers::resetStats);
        classStatsTrackers.values().forEach(HeroStatsTrackers::resetStats);
    }

    public static HeroStatsTrackers getStatsTrackers(HeroEnum heroEnum) {
        return heroStatsTrackers.get(heroEnum);
    }

    public static HeroStatsTrackers getStatsTrackers(ClassEnum classEnum) {
        return classStatsTrackers.get(classEnum);
    }

    public static double getAverageWinrate(ClassEnum classEnum) {
        return classWinrates.get(classEnum)/classPlayrates.get(classEnum);
    }
}

