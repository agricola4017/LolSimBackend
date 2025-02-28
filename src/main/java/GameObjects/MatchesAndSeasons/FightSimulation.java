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

public class FightSimulation {
    private HeroFactory hf;
    private List<Hero> team1;
    private List<Hero> team2;
    private List<MatchHero> team1MatchHeroes;
    private List<MatchHero> team2MatchHeroes;
    private List<Integer> attackingOrder;
    private List<Integer> defendingOrder;
    private int round;
    private Random random;

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
    public FightSimulation(HeroFactory hf, List<Hero> team1, List<Hero> team2) {
        this.hf = hf;
        this.team1 = team1;
        this.team2 = team2;
        this.round = 1;
        this.random = new Random();
    }

    public  void main(String[] args) {

        for (int i = 0; i <1; i++) { 
            //simulateSeason(hf);
            hf.resetStatsTrackers();
        }

        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            System.out.println(hf.getAverageWinrate(classEnum));
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
        
        for (Hero hero : team1) {
            team1MatchHeroes.add(hero.generateMatchHero(1));
        }
        for (Hero hero : team2) {
            team2MatchHeroes.add(hero.generateMatchHero(2));
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

    public void simulateRound(BattlePanel battlePanel) {
        while (!team1MatchHeroes.isEmpty() && !team2MatchHeroes.isEmpty()) {
            int team1Bound = team1MatchHeroes.size();
            int team2Bound = team2MatchHeroes.size();

            int attackingHero1 = random.nextInt(team1Bound);
            int attackingHero2 = random.nextInt(team2Bound);

            int defendingHero1 = random.nextInt(team1Bound);
            int defendingHero2 = random.nextInt(team2Bound);

            MatchHero attacker1 = team1MatchHeroes.get(attackingHero1);
            MatchHero defender2 = team2MatchHeroes.get(defendingHero2);
            MatchHero attacker2 = team2MatchHeroes.get(attackingHero2);
            MatchHero defender1 = team1MatchHeroes.get(defendingHero1);

            battlePanel.animateAttack(attacker1, defender2, attacker1.getAttack());
            battlePanel.animateAttack(attacker2, defender1, attacker2.getAttack());

            defender2.gotAttacked(attacker1.getAttack());
            defender1.gotAttacked(attacker2.getAttack());

            battlePanel.updateTeams(team1MatchHeroes, team2MatchHeroes);

            if (!defender1.isAlive()) {
                team1MatchHeroes.remove(defendingHero1);
            }
            if (!defender2.isAlive()) {
                team2MatchHeroes.remove(defendingHero2);
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
            winner = team2;
            loser = team1;
        } else {
           // System.out.println("Team 1 wins!");
            winner = team1;
            loser = team2;
        }

        for (int i = 0; i < winner.size(); i++) {
            hf.getStatsTrackers(winner.get(i).getHeroEnum().getClassEnum()).addWin();
            hf.getStatsTrackers(loser.get(i).getHeroEnum().getClassEnum()).addLoss();
        }

        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            HeroStatsTrackers statsTrackers = hf.getStatsTrackers(classEnum);
            double winrate = statsTrackers.getWins() / (float)(statsTrackers.getWins() + statsTrackers.getLosses());
            System.out.println(classEnum + " " + winrate);
        }

        HeroEnum.resetAvailableHeroes();
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
