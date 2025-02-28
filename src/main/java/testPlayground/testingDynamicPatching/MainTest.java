package testPlayground.testingDynamicPatching;

import javax.swing.SwingUtilities;

import Game.GameUI.TeamfightWindow;
import GameObjects.HerosAndClasses.Hero;
import GameObjects.HerosAndClasses.HeroFactory;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class MainTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HeroFactory hf = new HeroFactory();
            List<Hero> team1 = new ArrayList<>();
            List<Hero> team2 = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                team1.add(hf.createHero());
                team2.add(hf.createHero());
            }
            TeamfightWindow window = new TeamfightWindow(team1, team2);
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
