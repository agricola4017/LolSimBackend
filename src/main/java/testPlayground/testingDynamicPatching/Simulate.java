package testPlayground.testingDynamicPatching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulate {
    public static void main(String[] args) {
        HeroFactory hf = new HeroFactory();

        for (int i = 0; i <2; i++) { 
            simulateSeason(hf);
            hf.resetStatsTrackers();
        }

        for (ClassEnum classEnum : ClassEnum.getClassEnums()) {
            System.out.println(hf.getAverageWinrate(classEnum));
        }
    }

    public static void simulateSeason(HeroFactory hf) {
        for (int i = 0; i < 1000; i++) { 
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
    }

    public static void simulateMatch(HeroFactory hf) {
        List<Hero> team1 = new ArrayList<>();
        List<Hero> team2 = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            team1.add(hf.createHero());
            team2.add(hf.createHero());
        }

        List<MatchHero> team1MatchHeroes = new ArrayList<>();
        List<MatchHero> team2MatchHeroes = new ArrayList<>();
        
        for (Hero hero : team1) {
            team1MatchHeroes.add(hero.generateMatchHero());
        }
        for (Hero hero : team2) {
            team2MatchHeroes.add(hero.generateMatchHero());
        }
        //aggregateHeroCounts(team1, team2);

        Random random = new Random();
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

            defender2.gotAttacked(attacker1.getAttack());
            defender1.gotAttacked(attacker2.getAttack());

            if (!defender1.isAlive()) {
                team1MatchHeroes.remove(defendingHero1);
            }
            if (!defender2.isAlive()) {
                team2MatchHeroes.remove(defendingHero2);
            }
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
            StatsTrackers statsTrackers = hf.getStatsTrackers(classEnum);
            double winrate = statsTrackers.getWins() / (float)(statsTrackers.getWins() + statsTrackers.getLosses());
            //System.out.println("ClassEnum: " + classEnum + " winrate: " + winrate);
        }

        HeroEnum.resetAvailableHeroes();
    }

    public static void aggregateHeroCounts(List<Hero> team1, List<Hero> team2) {
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
}
