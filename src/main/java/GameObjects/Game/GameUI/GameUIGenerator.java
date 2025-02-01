package GameObjects.Game.GameUI;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import GameObjects.Game.Game;
import GameObjects.Game.Callbacks.PlayerFormCallback;
import GameObjects.Game.Callbacks.IDFormCallback;
import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;

import java.awt.Font;
import java.awt.FlowLayout;

public class GameUIGenerator {
    private static Map<String, JFrame> activeFrames = new HashMap<>();
    private static Map<String, JLabel> activeLabels = new HashMap<>();

    public static void createOrUpdateTextFrame(String frameId, String title, String message) {
        JFrame frame = activeFrames.get(frameId);
        JLabel messageLabel;

        if (frame == null) {
            // Create new frame if it doesn't exist
            frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLayout(new FlowLayout());

            // Create new label
            messageLabel = new JLabel();
            frame.add(messageLabel);

            // Store references
            activeFrames.put(frameId, frame);
            activeLabels.put(frameId, messageLabel);
            
            // Add window listener for cleanup
            addWindowListener(frameId, frame);
        } else {
            messageLabel = activeLabels.get(frameId);
            frame.setTitle(title);
        }

        // Update content
        messageLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
        
        // Update font size
        int newFontSize = Math.max(12, frame.getWidth() / 50);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));

        // Show frame if not visible
        if (!frame.isVisible()) {
            frame.setVisible(true);
        }

        // Revalidate and repaint
        frame.revalidate();
        frame.repaint();
    }

    public static void createPlayerSigningForm(PlayerFormCallback callback) {
        JFrame frame = new JFrame("Sign a Player");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(4, 2, 10, 10));

        // Create form components
        JLabel nameLabel = new JLabel("Player Name:");
        JTextField nameField = new JTextField();
        
        JLabel positionLabel = new JLabel("Position:");
        JComboBox<Position> positionComboBox = new JComboBox<>(Position.values());
        
        JLabel ovrLabel = new JLabel("Overall Rating:");
        JSpinner ovrSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        
        JButton submitButton = new JButton("Sign Player");

        // Add components to frame
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(positionLabel);
        frame.add(positionComboBox);
        frame.add(ovrLabel);
        frame.add(ovrSpinner);
        frame.add(new JLabel()); // Empty label for spacing
        frame.add(submitButton);

        // Add action listener to submit button
        submitButton.addActionListener(e -> {
            String name = nameField.getText();
            Position position = (Position) positionComboBox.getSelectedItem();
            int ovr = (Integer) ovrSpinner.getValue();
            
            // Call the callback with the form data
            callback.onPlayerSubmit(name, position, ovr);

            // Close the form
            frame.dispose();
        });

        // Display the frame
        frame.setVisible(true);
    }

    public static void createFindByIDForm(String identifier, String title, IDFormCallback callback) {
        JFrame frame = new JFrame(title);
        activeFrames.put(identifier, frame);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        JLabel idLabel = new JLabel(identifier);
        JTextField idField = new JTextField();
        idField.setColumns(10);


        frame.add(idLabel);
        frame.add(idField);

        JButton submitButton = new JButton("Find");
        frame.add(submitButton);

        submitButton.addActionListener(e -> {
            String id = idField.getText();
            int ID = Integer.parseInt(id);
            callback.onIDSubmit(ID);

        });

        frame.setVisible(true);
    }
    
    public static void updateIDForm(String frameId, String message) {
        JFrame frame = activeFrames.get(frameId);
        JLabel messageLabel;

        if (frame==null) {
            System.out.println("Frame not found: " + frameId);
            return;
        }


        frame.setSize(600,200);
        if (activeLabels.containsKey(frameId)) { 
            messageLabel = activeLabels.get(frameId);

        } else {
            messageLabel = new JLabel();
            activeLabels.put(frameId, messageLabel);
        }
        messageLabel.setText("<html>" + message.replace("\n", "<br>") + "</html>");
        
        // Update font size
        int newFontSize = Math.max(12, frame.getWidth() / 50);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));
        frame.add(messageLabel);

        if (!frame.isVisible()) {
            frame.setVisible(true);
        }

        // Revalidate and repaint
        frame.revalidate();
        frame.repaint();
    }

    // Method to check if a frame exists
    public static boolean hasFrame(String frameId) {
        return activeFrames.containsKey(frameId);
    }
    // Method to dispose a frame
    public static void disposeFrame(String frameId) {
        JFrame frame = activeFrames.remove(frameId);
        if (frame != null) {
            frame.dispose();
            activeLabels.remove(frameId);
        }
    }

    // Add window listener to remove from maps when closed
    private static void addWindowListener(String frameId, JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                activeFrames.remove(frameId);
                activeLabels.remove(frameId);
            }
        });
    }
}
