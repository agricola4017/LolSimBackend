package GameObjects.MatchesAndSeasons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Game.GameUI.TeamfightWindow;
import Game.GameUI.TeamfightWindow.BattlePanel;
import GameObjects.HerosAndClasses.ClassEnum;
import GameObjects.HerosAndClasses.Hero;
import GameObjects.HerosAndClasses.HeroEnum;
import GameObjects.HerosAndClasses.HeroFactory;
import GameObjects.HerosAndClasses.HeroStatsTrackers;
import GameObjects.TeamsAndPlayers.Team;

public class FightSimulation {
    private HeroFactory hf;
    private List<Hero> team1Heroes;
    private List<Hero> team2Heroes;
    private List<MatchHero> team1MatchHeroes;
    private List<MatchHero> team2MatchHeroes;
    private List<Integer> attackingOrder;
    private List<Integer> defendingOrder;
    private int round;
    private Random random;
    private Team winner;
    private Team[] teams;

     // Damage tracking data structures
     private Map<MatchHero, Map<MatchHero, Integer>> damageDealt; // Who dealt damage to whom
     private Map<MatchHero, MatchHero> killingBlows; // Who got the killing blow on whom
     private Map<MatchHero, Integer> totalDamageDealt; // Total damage dealt by each hero
     private Map<MatchHero, Integer> totalDamageTaken; // Total damage taken by each hero

    /**
     * presume teams have heroes in them 
     * for (int i = 0; i < 5; i++) {
            team1.add(hf.createHero());
            team2.add(hf.createHero());
        }
     * @param hf
     * @param team1
     * @param team2
     */
    public FightSimulation(Team[] teams,List<Hero> team1Heroes, List<Hero> team2Heroes) {
        this.team1Heroes = team1Heroes;
        this.team2Heroes = team2Heroes;
        this.teams = teams;
        this.round = 1;
        this.random = new Random();
        this.damageDealt = new HashMap<>();
        this.killingBlows = new HashMap<>();
        this.totalDamageDealt = new HashMap<>();
        this.totalDamageTaken = new HashMap<>();
    }

    public  void main(String[] args) {

        for (int i = 0; i <1; i++) { 
            //simulateSeason(hf);
            HeroFactory.resetStatsTrackers();
        }

        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            System.out.println(HeroFactory.getAverageWinrate(classEnum));
        }
    }

    /**
    public void simulateSeason(HeroFactory hf) {
        for (int i = 0; i < 1; i++) { 
            simulateMatch(hf);
        }

        //System.out.println("Tank" + " " + hf.getTankAttack() + " " + hf.getTankHP()); 
        //System.out.println("Fighter" + " " + hf.getFighterAttack() + " " + hf.getFighterHP());
        //System.out.println("Mage: ATK" + hf.getMageAttack() + " HP" + hf.getMageHP());
        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            //if (classEnum == ClassEnum.TANK) continue;
            //if (classEnum == ClassEnum.FIGHTER) continue;
            StatsTrackers statsTrackers = hf.getStatsTrackers(classEnum);
            double winrate = statsTrackers.getWins() / (float)(statsTrackers.getWins() + statsTrackers.getLosses());
            //System.out.println(classEnum + " winrate: " + winrate);
            hf.balanceLevers(classEnum, winrate, 0.5, statsTrackers.getWins() + statsTrackers.getLosses());
        }

        //System.out.println("Tank" + " " + hf.getTankAttack() + " " + hf.getTankHP()); 
        //System.out.println("Fighter" + " " + hf.getFighterAttack() + " " + hf.getFighterHP());
        //System.out.println("Mage: ATK" + ClassEnum.MAGE.getAttack() + " HP" + ClassEnum.MAGE.getHP());
    } */

    public void generateMatchHeroes() {
        team1MatchHeroes = new ArrayList<>();
        team2MatchHeroes = new ArrayList<>();
        
        for (Hero hero : team1Heroes) {
            MatchHero matchHero = hero.generateMatchHero(1);
            team1MatchHeroes.add(matchHero);

             // Initialize tracking data for this match hero
             damageDealt.put(matchHero, new HashMap<>());
             totalDamageDealt.put(matchHero, 0);
             totalDamageTaken.put(matchHero, 0);
        }
        for (Hero hero : team2Heroes) {
            MatchHero matchHero = hero.generateMatchHero(2);
            team2MatchHeroes.add(matchHero);

             // Initialize tracking data for this match hero
             damageDealt.put(matchHero, new HashMap<>());
             totalDamageDealt.put(matchHero, 0);
             totalDamageTaken.put(matchHero, 0);
        }
    }

    public List<MatchHero> getTeam1MatchHeroes() {
        return team1MatchHeroes;
    }
    public List<MatchHero> getTeam2MatchHeroes() {
        return team2MatchHeroes;
    }

    public void generateAttackingAndDefendingOrder() {
        /** shoud lbe based on speed an ddef */
        /** also consider making match hero a field of hero  */
    }

    public MatchHero getRandomHero(List<MatchHero> team) {
        int teamBound = team.size();
        int randomIndex = random.nextInt(teamBound);
        return team.get(randomIndex);
    }

    // Track damage between heroes and record killing blows
    private void recordDamage(MatchHero attacker, MatchHero defender, int damage) {
        // Update damage dealt from attacker to defender
        Map<MatchHero, Integer> attackerDamage = damageDealt.get(attacker);
        attackerDamage.put(defender, attackerDamage.getOrDefault(defender, 0) + damage);
        
        // Update total damage counters
        totalDamageDealt.put(attacker, totalDamageDealt.get(attacker) + damage);
        totalDamageTaken.put(defender, totalDamageTaken.get(defender) + damage);
        
        // Check if this damage killed the defender
        if (!defender.isAlive()) {
            // Record killing blow
            killingBlows.put(defender, attacker);
        }
    }
    
    public void simulateRound(BattlePanel battlePanel) {
        while (!team1MatchHeroes.isEmpty() && !team2MatchHeroes.isEmpty()) {
            MatchHero attacker1 = getRandomHero(team1MatchHeroes);
            MatchHero defender2 = getRandomHero(team2MatchHeroes);
            
            int damage = attacker1.getAttack();
            battlePanel.animateAttack(attacker1, defender2, damage);

            // Save HP before attack to calculate actual damage dealt
            int defenderHpBefore = defender2.getHp();
            defender2.gotAttacked(damage);

            int actualDamage = defenderHpBefore - defender2.getHp();

            recordDamage(attacker1, defender2, actualDamage);

            if (!defender2.isAlive()) {
                team2MatchHeroes.remove(defender2);
                battlePanel.addGraveyardTeam2(defender2);
            }

            if (!team2MatchHeroes.isEmpty() && !team1MatchHeroes.isEmpty()) {
                MatchHero attacker2 = getRandomHero(team2MatchHeroes);
                MatchHero defender1 = getRandomHero(team1MatchHeroes);

                damage = attacker2.getAttack();
                battlePanel.animateAttack(attacker2, defender1, damage);
                
                // Save HP before attack to calculate actual damage dealt
                defenderHpBefore = defender1.getHp();
                defender1.gotAttacked(damage);
                actualDamage = defenderHpBefore - defender1.getHp();
                
                recordDamage(attacker2, defender1, actualDamage);

                if (!defender1.isAlive()) {
                    team1MatchHeroes.remove(defender1);
                    battlePanel.addGraveyardTeam1(defender1);
                }
            }

            battlePanel.updateTeams(team1MatchHeroes, team2MatchHeroes);
        }
        if (team1MatchHeroes.isEmpty()) {
            this.winner = teams[0];
        } else if (team2MatchHeroes.isEmpty()) {
            this.winner = teams[1];
        }
    }

    public void simulateRound() {
        while (!team1MatchHeroes.isEmpty() && !team2MatchHeroes.isEmpty()) {
            MatchHero attacker1 = getRandomHero(team1MatchHeroes);
            MatchHero attacker2 = getRandomHero(team2MatchHeroes);

            MatchHero defender1 = getRandomHero(team1MatchHeroes);
            MatchHero defender2 = getRandomHero(team2MatchHeroes);

            int defender1HpBefore = defender1.getHp();
            int defender2HpBefore = defender2.getHp();

            defender2.gotAttacked(attacker1.getAttack());
            defender1.gotAttacked(attacker2.getAttack());

            int actualDamage1 = defender2HpBefore - defender2.getHp();
            int actualDamage2 = defender1HpBefore - defender1.getHp();

            recordDamage(attacker1, defender2, actualDamage1);
            recordDamage(attacker2, defender1, actualDamage2);

            if (!defender1.isAlive()) {
                team1MatchHeroes.remove(defender1);
            }
            if (!defender2.isAlive()) {
                team2MatchHeroes.remove(defender2);
            }
        }
    }

    public void simulateMatch(HeroFactory hf, BattlePanel battlePanel) {
        //aggregateHeroCounts(team1, team2);

        while (!team1MatchHeroes.isEmpty() && !team2MatchHeroes.isEmpty()) { 
            simulateRound(battlePanel);
        }

        List<Hero> winner;
        List<Hero> loser;
        if (team1MatchHeroes.isEmpty()) {
           // System.out.println("Team 2 wins!");
            winner = team2Heroes;
            loser = team1Heroes;
        } else {
           // System.out.println("Team 1 wins!");
            winner = team1Heroes;
            loser = team2Heroes;
        }

        for (int i = 0; i < winner.size(); i++) {
            HeroFactory.getStatsTrackers(winner.get(i).getHeroEnum().getClassEnum()).addWin();
            HeroFactory.getStatsTrackers(loser.get(i).getHeroEnum().getClassEnum()).addLoss();
        }

        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            HeroStatsTrackers statsTrackers = HeroFactory.getStatsTrackers(classEnum);
            double winrate = statsTrackers.getWins() / (float)(statsTrackers.getWins() + statsTrackers.getLosses());
            System.out.println(classEnum + " " + winrate);
        }

        HeroEnum.resetAvailableHeroes();
    }

    // Print detailed match statistics
    public String getMatchStatistics() {
        StringBuilder ret = new StringBuilder();
        ret.append("\n========== MATCH STATISTICS ==========");
        ret.append("\nTeam " + winner.getTeamName() + " won the match!");
        
        // Print MVP (most damage dealt)
        MatchHero mvp = null;
        int maxDamage = 0;
        
        for (Map.Entry<MatchHero, Integer> entry : totalDamageDealt.entrySet()) {
            if (entry.getValue() > maxDamage) {
                maxDamage = entry.getValue();
                mvp = entry.getKey();
            }
        }
        
        if (mvp != null) {
            ret.append("\nMVP: " + mvp.getClass() + " (Team " + mvp.getTeam() + ")");
            ret.append("\nTotal Damage: " + maxDamage);
            ret.append("\nKilling Blows: " + countKillingBlows(mvp));
        }
        
        // Print Team 1 stats
        ret.append("\n--- TEAM 1 STATISTICS ---");
        ret.append(getTeamStatistics(team1Heroes));
        
        // Print Team 2 stats
        ret.append("\n--- TEAM 2 STATISTICS ---");
        ret.append(getTeamStatistics(team2Heroes));
        
        // Print killing blow information
        ret.append("\n--- KILLING BLOWS ---");
        for (Map.Entry<MatchHero, MatchHero> entry : killingBlows.entrySet()) {
            MatchHero victim = entry.getKey();
            MatchHero killer = entry.getValue();
        }
        
        ret.append("=====================================\n");
        return ret.toString();
    }
    
    // Helper method to print statistics for a team
    private String getTeamStatistics(List<Hero> team) {
        StringBuilder ret = new StringBuilder();
        for (Hero hero : team) {
            for (MatchHero matchHero : damageDealt.keySet()) {
                if (matchHero.getHeroEnum() == hero.getHeroEnum()) {
                    ret.append(matchHero.getHeroEnum() + " (" + matchHero.getType() + "):");
                    ret.append("  Total Damage Dealt: " + totalDamageDealt.get(matchHero));
                    ret.append("  Total Damage Taken: " + totalDamageTaken.get(matchHero));
                    ret.append("  Killing Blows: " + countKillingBlows(matchHero));
                    
                    // Print damage breakdown if the hero dealt any damage
                    Map<MatchHero, Integer> herosDamage = damageDealt.get(matchHero);
                    if (!herosDamage.isEmpty()) {
                        ret.append("  Damage Breakdown:");
                        for (Map.Entry<MatchHero, Integer> entry : herosDamage.entrySet()) {
                            ret.append("    → " + entry.getKey().getHeroEnum() + " (Team " + entry.getKey().getTeam() + "): " + 
                                               entry.getValue() + " damage");
                        }
                    }
                    
                    // Check if this hero is still alive
                    if (matchHero.isAlive()) {
                        ret.append("  Status: Alive with " + matchHero.getHp() + " HP remaining");
                    } else {
                        MatchHero killer = killingBlows.get(matchHero);
                        if (killer != null) {
                            ret.append("  Status: Killed by " + killer.getHeroEnum() + 
                                               " (Team " + killer.getTeam() + ")");
                        } else {
                            ret.append("  Status: Dead");
                        }
                    }
                    break;
                }
            }
        }
        return ret.toString();
    }
    
    // Count how many killing blows a hero got
    private int countKillingBlows(MatchHero hero) {
        int count = 0;
        for (MatchHero killer : killingBlows.values()) {
            if (killer == hero) {
                count++;
            }
        }
        return count;
    }
    
    // Get who killed a specific hero (returns null if hero is still alive)
    public MatchHero getKiller(MatchHero hero) {
        return killingBlows.get(hero);
    }
    
    // Get total damage dealt by a hero
    public int getTotalDamageDealt(MatchHero hero) {
        return totalDamageDealt.getOrDefault(hero, 0);
    }
    
    // Get total damage taken by a hero
    public int getTotalDamageTaken(MatchHero hero) {
        return totalDamageTaken.getOrDefault(hero, 0);
    }

    public  void aggregateHeroCounts(List<Hero> team1, List<Hero> team2) {
        Map<ClassEnum, Integer> heroCountMapTeam1 = new HashMap<>();
        Map<ClassEnum, Integer> heroCountMapTeam2 = new HashMap<>();

        // Process team 1
        for (Hero hero : team1) {
            ClassEnum classEnum = hero.getClassEnum(); // Assuming you have a method to get ClassEnum
            heroCountMapTeam1.put(classEnum, heroCountMapTeam1.getOrDefault(classEnum, 0) + 1);
        }

        // Process team 2
        for (Hero hero : team2) {
            ClassEnum classEnum = hero.getClassEnum(); // Assuming you have a method to get ClassEnum
            heroCountMapTeam2.put(classEnum, heroCountMapTeam2.getOrDefault(classEnum, 0) + 1);
        }

        // Print the aggregated counts
        System.out.println("Hero counts by class:");
        for (Map.Entry<ClassEnum, Integer> entry : heroCountMapTeam1.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        for (Map.Entry<ClassEnum, Integer> entry : heroCountMapTeam2.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public int getRound() {
        return round;
    }
}
