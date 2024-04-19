package GameObjects.TeamsAndPlayers;

import static Functions.Functions.randomNumber0to100;

public class Stat {

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
