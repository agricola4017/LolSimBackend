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

import GameObjects.Game.Callbacks.PlayerFormCallback;
import GameObjects.Game.Callbacks.IDFormCallback;
import GameObjects.TeamsAndPlayers.Position;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Cursor;

public class GameUIGenerator {
    private JFrame mainFrame;
    private Map<String, JPanel> activePanels;
    private Map<String, JFrame> activeFrames;
    private Map<String, JLabel> activeLabels;

    public GameUIGenerator() {
        mainFrame = new JFrame("Game UI");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLayout(new FlowLayout());
        activePanels = new HashMap<>();
        activeFrames = new HashMap<>();
        activeLabels = new HashMap<>();

        mainFrame.setVisible(true);
    }

    public void createOrUpdateTextPanel(String panelId, String title, String message) {
        JPanel panel = activePanels.get(panelId);

        if (panel == null) {
            // Create new frame if it doesn't exist
            panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JLabel titleLabel = new JLabel(title);
            JTextArea messageArea = new JTextArea(message);
            messageArea.setEditable(false); // Make it read-only
            // Create new label
            JScrollPane scrollPane = new JScrollPane(messageArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            scrollPane.setPreferredSize(new Dimension(500,300));
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton closePanelButton = new JButton("Close " + panelId);
            closePanelButton.addActionListener(e -> {
                activePanels.get(panelId).setVisible(false);
            });

            // Create a resize handle
            JPanel resizeHandle = new JPanel();
            resizeHandle.setPreferredSize(new Dimension(10, 10)); // Size of the handle
            resizeHandle.setBackground(Color.GRAY); // Color of the handle
            resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)); // Change cursor on hover
            resizeHandle.setFocusable(true);
            // Add mouse listener for resizing
            resizeHandle.addMouseListener(new MouseAdapter() {
                private Point initialClick;

                @Override
                public void mousePressed(MouseEvent e) {
                    // Get the initial click point
                    System.out.println("Resizing " + panelId + " at " + e.getPoint());
                    initialClick = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    // Get the current size of the panel
                    JPanel panel = activePanels.get(panelId);
                    int newWidth = panel.getWidth() + (e.getX() - initialClick.x);
                    int newHeight = panel.getHeight() + (e.getY() - initialClick.y);
                    // Set the new size
                    panel.setPreferredSize(new Dimension(newWidth, newHeight));
                    panel.revalidate(); // Revalidate the panel
                    panel.repaint(); // Repaint the panel
                }
            });

            // Add the new panel to the parent frame
            panel.add(resizeHandle, BorderLayout.EAST);
            panel.add(closePanelButton, BorderLayout.SOUTH);
            mainFrame.add(panel);
            activePanels.put(panelId, panel);
        } else {
            JTextArea messageArea = (JTextArea) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
            messageArea.setText(message);
        }

        // Show frame if not visible
        if (!panel.isVisible()) {
            panel.setVisible(true);
        }

        // Revalidate and repaint
        panel.revalidate();
        panel.repaint();
    }

    public void createPlayerSigningForm(PlayerFormCallback callback) {
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

    public void createFindByIDForm(String identifier, String title, IDFormCallback callback) {
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
    
    public void updateIDForm(String frameId, String message) {
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
        //messageLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));
        frame.add(messageLabel);

        if (!frame.isVisible()) {
            frame.setVisible(true);
        }

        // Revalidate and repaint
        frame.revalidate();
        frame.repaint();
    }

    // Method to check if a frame exists
    public boolean hasFrame(String frameId) {
        return activeFrames.containsKey(frameId);
    }
    // Method to dispose a frame
    public void disposeFrame(String frameId) {
        JFrame frame = activeFrames.remove(frameId);
        if (frame != null) {
            frame.dispose();
            activeLabels.remove(frameId);
        }
    }

    // Add window listener to remove from maps when closed
    private void addWindowListener(String frameId, JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                activeFrames.remove(frameId);
                activeLabels.remove(frameId);
            }
        });
    }
}
