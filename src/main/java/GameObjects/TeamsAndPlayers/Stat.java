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
            this.potential = Math.min(100, this.getOVR() + randomNumberSlowFast(0, 100-this.getOVR()) + randomValue);
        } else if (age <= 24) {
            this.potential = Math.min(100, this.getOVR() + randomNumberSlowFast(0, 100-this.getOVR()) + randomValue);
        } else if (age <= 28) {
            this.potential = Math.min(100, this.getOVR() + randomNumberSlowFast(0, 100-this.getOVR()) + randomValue);
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
            increaseStats(randomValue);
        } 

        randomValue = (int) (Math.random() * 100); // Generate random number between 0 and 99

        if (randomValue < declineChance) {
            decreaseStats(randomValue);
        }

        int delta = this.getOVR() - old;
        if (delta >= 0) {
            delta = (int)Math.round(Math.random() * delta);
            this.potential = Math.min(100, this.potential + delta);
            this.potential = Math.max(this.getOVR(), this.potential);
        } else {
            delta = (int)Math.round(Math.random() * delta);
            this.potential = Math.max(0, this.potential + delta); 
        }
    }
    public void increaseStats(int randomValue) {
        int delta = this.potential - this.getOVR();
        
        this.laning = Math.min(100, this.laning + randomNumberSlowFast(1,delta));
        this.teamfighting = Math.min(100, this.teamfighting + randomNumberSlowFast(1,delta));
        this.economy = Math.min(100, this.economy + randomNumberSlowFast(1,delta));
        this.consistency = Math.min(100, this.consistency + randomNumberSlowFast(1,delta));
        this.teamwork = Math.min(100, this.teamwork + randomNumberSlowFast(1,delta));
        this.aggression = Math.min(100, this.aggression + randomNumberSlowFast(1,delta));
        this.stamina = Math.min(100, this.stamina + randomNumberSlowFast(1,delta));
    }

    public void decreaseStats(int randomValue) {
        //decrease should not actually use POT, we should have big increases but NOT big decreases 
        int delta = this.potential - this.getOVR();
        this.laning = Math.max(0, this.laning - randomNumberSlowFast(1,delta));
        this.teamfighting = Math.max(0, this.teamfighting - randomNumberSlowFast(1,delta));
        this.economy = Math.max(0, this.economy - randomNumberSlowFast(1,delta));
        this.consistency = Math.max(0, this.consistency - randomNumberSlowFast(1,delta));
        this.teamwork = Math.max(0, this.teamwork - randomNumberSlowFast(1,delta));
        this.aggression = Math.max(0, this.aggression - randomNumberSlowFast(1,delta));
        this.stamina = Math.max(0, this.stamina - randomNumberSlowFast(1,delta));
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
        return this.potential;
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
