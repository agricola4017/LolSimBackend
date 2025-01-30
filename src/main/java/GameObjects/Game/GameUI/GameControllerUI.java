package GameObjects.Game.GameUI;

import javax.swing.*;
import java.awt.*;

public class GameControllerUI {

    private JButton playSeasonButton;
    private JButton playGameButton;
    private JButton seeTeamInfoButton;
    private JButton seeStandingsButton;
    private JButton seePlayersButton;
    private JButton signPlayerButton;
    private JButton playTeamGameButton;
    private JButton findPlayerButton;

    public GameControllerUI() {
        // Create the frame
        JFrame frame = new JFrame("Game Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new FlowLayout());

        // Create buttons for each option
        playSeasonButton = new JButton("Play Season");
        playGameButton = new JButton("Play Game");
        seeTeamInfoButton = new JButton("See Team Info");
        seeStandingsButton = new JButton("See Standings");
        seePlayersButton = new JButton("See Players");
        signPlayerButton = new JButton("Sign Player");
        playTeamGameButton = new JButton("Play Team Game");
        findPlayerButton = new JButton("Find Player");
        // Add buttons to the frame
        frame.add(playSeasonButton);
        frame.add(playGameButton);
        frame.add(seeTeamInfoButton);
        frame.add(seeStandingsButton);
        frame.add(seePlayersButton);
        frame.add(signPlayerButton);
        frame.add(playTeamGameButton);
        frame.add(findPlayerButton);

        // Set frame visibility
        frame.setVisible(true);
    }

    public JButton getPlaySeasonButton() {
        return playSeasonButton;
    }
    
    public JButton getPlayGameButton() {
        return playGameButton;
    }
    
    public JButton getSeeTeamInfoButton() {
        return seeTeamInfoButton;
    }
    
    public JButton getSeeStandingsButton() {
        return seeStandingsButton;
    }
    
    public JButton getSeePlayersButton() {
        return seePlayersButton;
    }
    
    public JButton getSignPlayerButton() {
        return signPlayerButton;
    }

    public JButton getPlayTeamGameButton() {
        return playTeamGameButton;
    }

    public JButton getFindPlayerButton() {
        return findPlayerButton;
    }
}
