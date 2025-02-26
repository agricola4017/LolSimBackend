package testPlayground.testingDynamicPatching;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HeroFactory {

    private Map<HeroEnum, StatsTrackers> statsTrackersMap;

    private static int FIGHTER_HP;
    private static int FIGHTER_ATTACK;

    private static int MAGE_HP;
    private static int MAGE_ATTACK;

    private static int TANK_HP;
    private static int TANK_ATTACK;

    private final static int DEFAULT_LEVER_FACTOR = 10;

    public HeroFactory() {
        statsTrackersMap = new HashMap<>();
        for (HeroEnum heroEnum : HeroEnum.getHeroEnums()) {
            statsTrackersMap.put(heroEnum, new StatsTrackers(0, 0));
        }
        
        FIGHTER_ATTACK = Fighter.DEFAULT_ATTACK;
        FIGHTER_HP = Fighter.DEFAULT_HP;

        MAGE_ATTACK = Mage.DEFAULT_ATTACK;
        MAGE_HP = Mage.DEFAULT_HP;

        TANK_ATTACK = Tank.DEFAULT_ATTACK;
        TANK_HP = Tank.DEFAULT_HP;

    }

    public Hero createHero(HeroEnum heroEnum) {
        switch (heroEnum) {
            case FIGHTER:
                return new Fighter(FIGHTER_HP, FIGHTER_ATTACK);
            case MAGE:
                return new Mage(MAGE_HP, MAGE_ATTACK);
            case TANK:
                return new Tank(TANK_HP, TANK_ATTACK);
        }
        throw new IllegalArgumentException("Invalid hero enum: " + heroEnum);
    }

    public StatsTrackers getStatsTrackers(HeroEnum heroEnum) {
        return statsTrackersMap.get(heroEnum);
    }

    public void balanceLevers(HeroEnum heroEnum, double current, double target) {
        int attack;
        int hp;
        switch (heroEnum) {
            case FIGHTER:
                attack = FIGHTER_ATTACK;
                hp = FIGHTER_HP;
                break;
            case MAGE:
                attack = MAGE_ATTACK;
                hp = MAGE_HP;
                break;
            case TANK:
                attack = TANK_ATTACK;
                hp = TANK_HP;
                break;
            default:
                throw new IllegalArgumentException("Invalid hero enum: " + heroEnum);
        }
        Random random = new Random();
        double randomHpFactor = random.nextGaussian((float)hp/(hp+attack), 0.03);
        double growthFactor = (target - current)/DEFAULT_LEVER_FACTOR;
        double randomAttackFactor = 1- randomHpFactor;
        System.out.println("buff = " + (target > current));
        System.out.println(growthFactor);
        //System.out.println(hp*randomHpFactor + " " + (target - current) + " " + DEFAULT_LEVER_FACTOR);
        double balanceFactor = hp*randomHpFactor * growthFactor;
        int newHp = (int)(hp * randomHpFactor + balanceFactor);
        int newAttack = (int)(attack * randomAttackFactor + balanceFactor);
        hp = newHp;
        attack = newAttack;

        switch (heroEnum) {
            case FIGHTER:
                FIGHTER_HP = hp;
                FIGHTER_ATTACK = attack;
                break;
            case MAGE:
                MAGE_HP = hp;
                MAGE_ATTACK = attack;
                break;
            case TANK:
                TANK_HP = hp;
                TANK_ATTACK = attack;
                break;
        }
    }

    public int getFighterHP() {
        return FIGHTER_HP;
    }

    public int getFighterAttack() {
        return FIGHTER_ATTACK;
    }   

    public int getMageHP() {
        return MAGE_HP;
    }

    public int getMageAttack() {
        return MAGE_ATTACK;
    }

    public int getTankHP() {
        return TANK_HP;
    }

    public int getTankAttack() {
        return TANK_ATTACK;
    }
}

