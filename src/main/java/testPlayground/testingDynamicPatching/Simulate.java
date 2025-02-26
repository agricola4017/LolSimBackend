package testPlayground.testingDynamicPatching;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulate {
    public static void main(String[] args) {

        HeroFactory hf = new HeroFactory();
        Hero team1fighter = hf.createHero(HeroEnum.FIGHTER);
        Hero team1mage = hf.createHero(HeroEnum.MAGE);
        Hero team1tank = hf.createHero(HeroEnum.TANK);
        Hero team1tank2 = hf.createHero(HeroEnum.TANK);
        Hero team1figher2 = hf.createHero(HeroEnum.FIGHTER);

        List<Hero> team1 = new ArrayList<>();
        team1.add(team1fighter);
        team1.add(team1mage);
        team1.add(team1tank);
        team1.add(team1tank2);
        team1.add(team1figher2);

        Hero team2fighter = hf.createHero(HeroEnum.FIGHTER);
        Hero team2mage = hf.createHero(HeroEnum.MAGE);
        Hero team2mage2 = hf.createHero(HeroEnum.MAGE);
        Hero team2tank = hf.createHero(HeroEnum.TANK);
        Hero team2figher2 = hf.createHero(HeroEnum.FIGHTER);

        List<Hero> team2 = new ArrayList<>();
        team2.add(team2fighter);
        team2.add(team2mage);
        team2.add(team2mage2);
        team2.add(team2tank);
        team2.add(team2figher2);

        List<MatchHero> team1MatchHeroes = new ArrayList<>();
        List<MatchHero> team2MatchHeroes = new ArrayList<>();
        
        for (Hero hero : team1) {
            team1MatchHeroes.add(hero.generateMatchHero());
        }
        for (Hero hero : team2) {
            team2MatchHeroes.add(hero.generateMatchHero());
        }

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
            System.out.println("Team 2 wins!");
            winner = team2;
            loser = team1;
        } else {
            System.out.println("Team 1 wins!");
            winner = team1;
            loser = team2;
        }

        for (int i = 0; i < winner.size(); i++) {
            hf.getStatsTrackers(winner.get(i).getHeroEnum()).addWin();
            hf.getStatsTrackers(loser.get(i).getHeroEnum()).addLoss();
        }
        
        System.out.println("Tank" + " " + hf.getTankAttack() + " " + hf.getTankHP()); 
        System.out.println("Fighter" + " " + hf.getFighterAttack() + " " + hf.getFighterHP());
        System.out.println("Mage" + " " + hf.getMageAttack() + " " + hf.getMageHP());
        for (HeroEnum heroEnum : HeroEnum.getHeroEnums()) {
            StatsTrackers statsTrackers = hf.getStatsTrackers(heroEnum);
            double winrate = statsTrackers.getWins() / (float)(statsTrackers.getWins() + statsTrackers.getLosses());
            System.out.println(heroEnum + ": " + winrate);
            hf.balanceLevers(heroEnum, winrate, 0.5);
        }

        System.out.println("Tank" + " " + hf.getTankAttack() + " " + hf.getTankHP()); 
        System.out.println("Fighter" + " " + hf.getFighterAttack() + " " + hf.getFighterHP());
        System.out.println("Mage" + " " + hf.getMageAttack() + " " + hf.getMageHP());
    }
}
