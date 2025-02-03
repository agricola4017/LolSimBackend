package GameObjects.TeamsAndPlayers;

import static Functions.Functions.randomNumber0to100;
import static Functions.Functions.randomNumberCustom;
import static Functions.Functions.randomNumberSlowFast;

import java.io.Serializable;

public class Stat implements Serializable{

    private int laning;
    private int teamfighting;
    private int economy;
    private int consistency;
    private int teamwork;
    private int aggression;
    private int stamina;
    private int potential;

    public Stat() {
        this.laning = 0;
        this.teamfighting = 0;
        this.economy = 0;
        this.consistency = 0;
        this.teamwork = 0;
        this.aggression = 0;
        this.stamina = 0;
        this.potential = 50;
    }

    public Stat(int laning, int teamfighting, int economy, int consistency, int teamwork, int aggression, int stamina, int potential) {
        this.laning = laning;
        this.teamfighting = teamfighting;
        this.economy = economy;
        this.consistency = consistency;
        this.teamwork = teamwork;
        this.aggression = aggression;
        this.stamina = stamina;
        this.potential = potential;
    }

    public static Stat generateRandomStats() {
        int laning = randomNumber0to100();
        int teamfighting = randomNumber0to100();
        int economy = randomNumber0to100();
        int consistency = randomNumber0to100();
        int teamwork = randomNumber0to100();
        int aggression = randomNumber0to100();
        int stamina = randomNumber0to100();
        int potential = randomNumber0to100();

        return new Stat(laning, teamfighting, economy, consistency, teamwork, aggression, stamina, potential);
    }

    public static Stat generateRandomStats(int OVR) {
        int totalStats = 0;
        int[] stats = new int[7]; // 7 stats to distribute
        int min = 20;
        int max = 80;

        if (randomNumber0to100() < 10) {
            min = 0;
            max = 100;
        }

        // Generate random stats while ensuring they are bounded
        for (int i = 0; i < stats.length; i++) {
            stats[i] = randomNumberCustom(min, max);
            totalStats += stats[i];
        }
    
        // Adjust the stats to match the target OVR
        int adjustment = OVR - (totalStats / stats.length);
        for (int i = 0; i < stats.length; i++) {
            stats[i] += adjustment;
            // Ensure stats remain within bounds
            if (stats[i] < 0) stats[i] = 0;
            if (stats[i] > 100) stats[i] = 100;
        }
    
        return new Stat(stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6], 50);
    }

    public static Stat generatePerfectStat() {
        int laning = 100;
        int teamfighting = 100;
        int economy = 100;
        int consistency = 100;
        int teamwork = 100;
        int aggression = 100;
        int stamina = 100;
        int potential = 100;

        return new Stat(laning, teamfighting, economy, consistency, teamwork, aggression, stamina, potential);
    }

    public void calculatePotential(int age) {
        randomNumberCustom(0, 10);

        // random effect
        int randomValue = randomNumberSlowFast(0, 20);
        // flat effect
        if (age <= 20) {
            this.potential = Math.min(100, this.getOVR() + 15 + randomValue);
        } else if (age <= 24) {
            this.potential = Math.min(100, this.getOVR() + 10 + randomValue);
        } else if (age <= 28) {
            this.potential = Math.min(100, this.getOVR() + 5 + randomValue);
        } else { 
            this.potential = Math.min(100, this.getOVR() + randomValue);
        }
    }

    public void changeStats(int improvementChance, int declineChance) {
        //needs more complex knowledge, not just spread random
        //for example, old player gets much better at economy specifically but worse at aggression

        int old = this.getOVR();
             
        int randomValue = (int) (Math.random() * 100); // Generate random number between 0 and 99
        
        if (randomValue < improvementChance) {
            increaseStats();
        } 

        randomValue = (int) (Math.random() * 100); // Generate random number between 0 and 99

        if (randomValue < declineChance) {
            decreaseStats();
        }

        int delta = this.getOVR() - old;
        this.potential = Math.min(100, this.potential + delta);
    }
    public void increaseStats() {
        int delta = this.potential - this.getOVR();
        this.laning += randomNumberCustom(1,delta);
        this.teamfighting += randomNumberCustom(1,delta);
        this.economy += randomNumberCustom(1,delta);
        this.consistency += randomNumberCustom(1,delta);
        this.teamwork += randomNumberCustom(1,delta);
        this.aggression += randomNumberCustom(1,delta);
        this.stamina += randomNumberCustom(1,delta);
        this.potential += delta;
    }

    public void decreaseStats() {
        //decrease should not actually use POT, we should have big increases but NOT big decreases 
        int delta = this.potential - this.getOVR();
        this.laning -= randomNumberCustom(1,delta);
        this.teamfighting -= randomNumberCustom(1,delta);
        this.economy -= randomNumberCustom(1,delta);
        this.consistency -= randomNumberCustom(1,delta);
        this.teamwork -= randomNumberCustom(1,delta);
        this.aggression -= randomNumberCustom(1,delta);
        this.stamina -= randomNumberCustom(1,delta);
        this.potential -= delta;
    }
    public int getLaning() {
        return laning;
    }

    public void setLaning(int laning) {
        this.laning = laning;
    }

    public int getTeamfighting() {
        return teamfighting;
    }

    public void setTeamfighting(int teamfighting) {
        this.teamfighting = teamfighting;
    }

    public int getEconomy() {
        return economy;
    }

    public void setEconomy(int economy) {
        this.economy = economy;
    }

    public int getConsistency() {
        return consistency;
    }

    public void setConsistency(int consistency) {
        this.consistency = consistency;
    }

    public int getTeamwork() {
        return teamwork;
    }

    public void setTeamwork(int teamwork) {
        this.teamwork = teamwork;
    }

    public int getAggression() {
        return aggression;
    }

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getPotential() {
        return potential;
    }

    public void setPotential(int potential) {
        this.potential = potential;
    }

    public int getOVR() {
        return (laning + teamfighting + economy + consistency + teamwork + aggression + stamina)/7;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "laning=" + laning +
                ", teamfighting=" + teamfighting +
                ", economy=" + economy +
                ", consistency=" + consistency +
                ", teamwork=" + teamwork +
                ", aggression=" + aggression +
                ", stamina=" + stamina +
                ", potential=" + potential +
                '}';
    }
}
