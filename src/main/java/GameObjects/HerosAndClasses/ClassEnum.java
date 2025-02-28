package testPlayground.testingDynamicPatching;

import java.util.Random;

public enum ClassEnum {
    FIGHTER(100, 20),
    MAGE(60, 35),
    TANK(250, 8);

    private final int defaultHP;
    private final int defaultAttack;
    private int hp;
    private int attack;

    ClassEnum(int hp, int attack) {
        this.defaultHP = hp;
        this.defaultAttack = attack;
        this.hp = hp;
        this.attack = attack;
    }

    public int getHP() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public static ClassEnum[] getClassEnums() {
        return values();
    }

    public int getDefaultHP() {
        return defaultHP;
    }

    public int getDefaultAttack() {
        return defaultAttack;
    }

    public static ClassEnum random() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
