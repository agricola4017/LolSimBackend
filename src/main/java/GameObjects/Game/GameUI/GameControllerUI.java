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
    private JButton findTeamButton;
    private JButton saveGameButton;
    private JButton loadGameButton;

    public GameControllerUI() {
        // Create the frame
        JFrame frame = new JFrame("Game Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        //frame.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Create buttons for each option
        JPanel playPanel = new JPanel(new FlowLayout());
        playSeasonButton = new JButton("Play Season");
        playGameButton = new JButton("Play Game");
        playTeamGameButton = new JButton("Play Team Game");
        playPanel.add(playSeasonButton);
        playPanel.add(playGameButton);
        playPanel.add(playTeamGameButton);

        JPanel seePanel = new JPanel(new FlowLayout());
        seeTeamInfoButton = new JButton("See Team Info");
        seeStandingsButton = new JButton("See Standings");
        seePlayersButton = new JButton("See Players");
        seePanel.add(seeTeamInfoButton);
        seePanel.add(seeStandingsButton);
        seePanel.add(seePlayersButton);

        JPanel signFindPanel = new JPanel(new FlowLayout());
        signPlayerButton = new JButton("Sign Player");
        findPlayerButton = new JButton("Find Player");
        findTeamButton = new JButton("Find Team");
        signFindPanel.add(signPlayerButton);
        signFindPanel.add(findPlayerButton);
        signFindPanel.add(findTeamButton);

        JPanel saveLoadPanel = new JPanel(new FlowLayout());
        saveGameButton = new JButton("Save Game");
        loadGameButton = new JButton("Load Game");
        saveLoadPanel.add(saveGameButton);
        saveLoadPanel.add(loadGameButton);

        // Add buttons to the frame
        frame.add(playPanel);
        frame.add(seePanel);
        frame.add(signFindPanel);
        frame.add(saveLoadPanel);

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

    public JButton getFindTeamButton() {
        return findTeamButton;
    }

    public JButton getSaveGameButton() {
        return saveGameButton;
    }

    public JButton getLoadGameButton() {
        return loadGameButton;
    }
}
