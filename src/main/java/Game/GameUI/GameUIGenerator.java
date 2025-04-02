package Game.GameUI;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import Game.Callbacks.IDFormCallback;
import Game.Callbacks.PlayerFormCallback;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

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
import javax.swing.JTable;
import javax.swing.BorderFactory;
import java.awt.Font;


public class GameUIGenerator {
    private JFrame mainFrame;
    private Map<String, JPanel> activePanels;
    private Map<String, JFrame> activeFrames;
    private Map<String, JLabel> activeLabels;
    private static final Dimension DEFAULT_DIMENSION = new Dimension(500,300);

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

    public void createOrUpdateTextTablePanel(String panelId, String title, String message, String[][] data, String[] columnNames) {
        JPanel panel = activePanels.get(panelId);

        if (panel == null) {
            // Create new panel if it doesn't exist
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(DEFAULT_DIMENSION);

            JLabel titleLabel = new JLabel(title);
            panel.add(titleLabel, BorderLayout.NORTH);

            // Create a container panel for message and table
            JPanel contentPanel = new JPanel(new BorderLayout());
            
            // Add message area if provided
            if (!message.isEmpty()) {
                JTextArea messageArea = new JTextArea(message);
                messageArea.setEditable(false);
                messageArea.setBackground(new Color(230, 240, 255)); // Light blue background
                messageArea.setForeground(Color.BLUE); // Blue text
                messageArea.setFont(new Font("Arial", Font.BOLD, 14));
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                JScrollPane messageScrollPane = new JScrollPane(messageArea);
                messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                messageScrollPane.setPreferredSize(new Dimension(500, 30)); // Reduced height from 50 to 30
                
                contentPanel.add(messageScrollPane, BorderLayout.NORTH);
            }
            
            JButton closePanelButton = new JButton("Close " + panelId);
            closePanelButton.addActionListener(e -> {
                activePanels.get(panelId).setVisible(false);
            });

            // Create a resize handle
            JPanel resizeHandle = new JPanel();
            resizeHandle.setPreferredSize(new Dimension(10, 10));
            resizeHandle.setBackground(Color.GRAY);
            resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            resizeHandle.setFocusable(true);

            final Point[] initialClick = new Point[1];
            resizeHandle.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClick[0] = e.getPoint();
                }
            });
            resizeHandle.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    JPanel panel = activePanels.get(panelId);
                    final double sensitivity = 0.1;
                    
                    int newWidth = panel.getWidth() + (int) ((e.getX() - initialClick[0].x) * sensitivity);
                    int newHeight = panel.getHeight() + (int) ((e.getY() - initialClick[0].y) * sensitivity);
                    
                    newWidth = Math.max(newWidth, 100);
                    newHeight = Math.max(newHeight, 100);
                    newWidth = Math.min(newWidth, 400);
                    newHeight = Math.min(newHeight, 400);
                    
                    panel.setPreferredSize(new Dimension(newWidth, newHeight));
                    panel.revalidate();
                    panel.repaint();
                }
            });

            panel.add(resizeHandle, BorderLayout.EAST);
            panel.add(closePanelButton, BorderLayout.SOUTH);
            panel.add(contentPanel, BorderLayout.CENTER);
            mainFrame.add(panel);
            activePanels.put(panelId, panel);
        } else {
            // Update existing panel
            // First, remove the old content panel
            for (int i = 0; i < panel.getComponentCount(); i++) {
                if (panel.getComponent(i) instanceof JPanel) {
                    panel.remove(i);
                    break;
                }
            }

            // Create a container panel for message and table
            JPanel contentPanel = new JPanel(new BorderLayout());
            
            // Add message area if provided
            if (!message.isEmpty()) {
                JTextArea messageArea = new JTextArea(message);
                messageArea.setEditable(false);
                messageArea.setBackground(new Color(230, 240, 255)); // Light blue background
                messageArea.setForeground(Color.BLUE); // Blue text
                messageArea.setFont(new Font("Arial", Font.BOLD, 14));
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                JScrollPane messageScrollPane = new JScrollPane(messageArea);
                messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                messageScrollPane.setPreferredSize(new Dimension(500, 30)); // Reduced height from 50 to 30
                
                contentPanel.add(messageScrollPane, BorderLayout.NORTH);
            }
            
            panel.add(contentPanel, BorderLayout.CENTER);
        }

        // Create table with the provided data
        JTable table = new JTable(data, columnNames);
        
        // Disable row selection
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(java.awt.Color.WHITE);
        table.setSelectionForeground(java.awt.Color.BLACK);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        
        // Calculate available height for the table
        int panelHeight = panel.getHeight();
        int titleHeight = 0;
        int buttonHeight = 0;
        int messageHeight = 0;
        
        // Find the heights of title, message, and button components
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) instanceof JLabel) {
                titleHeight = panel.getComponent(i).getHeight();
            } else if (panel.getComponent(i) instanceof JButton) {
                buttonHeight = panel.getComponent(i).getHeight();
            } else if (panel.getComponent(i) instanceof JPanel) {
                JPanel contentPanel = (JPanel) panel.getComponent(i);
                for (int j = 0; j < contentPanel.getComponentCount(); j++) {
                    if (contentPanel.getComponent(j) instanceof JScrollPane) {
                        messageHeight = contentPanel.getComponent(j).getHeight();
                    }
                }
            }
        }
        
        // Calculate available height for the table (accounting for borders and padding)
        int availableHeight = panelHeight - titleHeight - messageHeight - buttonHeight - 20; // 20 pixels for padding
        
        // Calculate how many empty rows we need to fill the available space
        int rowHeight = table.getRowHeight();
        int visibleRows = availableHeight / rowHeight;
        int currentRows = data.length;
        int emptyRowsNeeded = Math.max(0, visibleRows - currentRows);
        
        // Create a new data array with empty rows
        String[][] newData = new String[currentRows + emptyRowsNeeded][columnNames.length];
        
        // Copy the existing data
        for (int i = 0; i < currentRows; i++) {
            for (int j = 0; j < columnNames.length; j++) {
                newData[i][j] = data[i][j];
            }
        }
        
        // Create a new table model with the expanded data
        DefaultTableModel model = new DefaultTableModel(newData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                // Return String.class for all columns to ensure consistent sorting
                return String.class;
            }
        };
        
        table.setModel(model);
        table.setFillsViewportHeight(true); // Make the table fill the available height
        
        // Enable sorting but don't set any default sort order
        table.setAutoCreateRowSorter(true);
        
        // Set custom cell renderer for clickable cells (team name columns)
        table.setDefaultRenderer(String.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Check if this is a clickable cell (team name columns)
                boolean isClickable = (column == 2 || column == 3) && value != null && !value.toString().isEmpty();
                
                // Set the cell's appearance
                if (isClickable) {
                    c.setForeground(java.awt.Color.BLUE);
                    if (c instanceof javax.swing.JLabel) {
                        javax.swing.JLabel label = (javax.swing.JLabel) c;
                        label.setText("<html><u>" + value + "</u></html>");
                    }
                } else {
                    c.setForeground(java.awt.Color.BLACK);
                    if (c instanceof javax.swing.JLabel) {
                        javax.swing.JLabel label = (javax.swing.JLabel) c;
                        label.setText(value != null ? value.toString() : "");
                    }
                }
                
                // Handle hover effect
                java.awt.Point mousePoint = table.getMousePosition();
                if (mousePoint != null) {
                    int mouseRow = table.rowAtPoint(mousePoint);
                    int mouseCol = table.columnAtPoint(mousePoint);
                    if (row == mouseRow && column == mouseCol) {
                        // Use a light blue background for hover effect
                        c.setBackground(new java.awt.Color(230, 240, 255));
                        // Set cursor based on whether cell is clickable
                        c.setCursor(isClickable ? 
                            java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR) : 
                            java.awt.Cursor.getDefaultCursor());
                    } else {
                        c.setBackground(java.awt.Color.WHITE);
                        c.setCursor(java.awt.Cursor.getDefaultCursor());
                    }
                } else {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setCursor(java.awt.Cursor.getDefaultCursor());
                }
                
                return c;
            }
        });
        
        // Add mouse listener for hover effect and clickable cells
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col >= 0) {
                    // Only set hand cursor for clickable cells
                    boolean isClickable = (col == 2 || col == 3) && 
                        table.getValueAt(table.convertRowIndexToModel(row), col) != null && 
                        !table.getValueAt(table.convertRowIndexToModel(row), col).toString().isEmpty();
                    table.setCursor(isClickable ? 
                        java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR) : 
                        java.awt.Cursor.getDefaultCursor());
                }
                table.repaint();
            }
            
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                table.repaint();
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col >= 0) {
                    // Only handle clicks on team name columns
                    if (col == 2 || col == 3) {
                        // Convert row index to model index for sorted tables
                        int modelRow = table.convertRowIndexToModel(row);
                        String cellValue = (String) table.getValueAt(modelRow, col);
                        // Only show details for non-empty rows
                        if (cellValue != null && !cellValue.isEmpty()) {
                            showTeamDetails(cellValue);
                        }
                    }
                }
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                table.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                table.setCursor(java.awt.Cursor.getDefaultCursor());
                table.repaint();
            }
        });
        
        // Add the table to a scroll pane
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Add the table to the content panel
        JPanel contentPanel = (JPanel) panel.getComponent(panel.getComponentCount() - 1);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        panel.revalidate();
        panel.repaint();
    }

    private void showTeamDetails(String teamName) {
        // Create a new panel for team details
        String panelId = "teamDetails_" + teamName;
        final JPanel panel = activePanels.get(panelId);

        if (panel == null) {
            JPanel newPanel = new JPanel();
            newPanel.setLayout(new BorderLayout());
            newPanel.setPreferredSize(DEFAULT_DIMENSION);

            JLabel titleLabel = new JLabel("Team Details: " + teamName);
            newPanel.add(titleLabel, BorderLayout.NORTH);

            // Add team details here
            JTextArea detailsArea = new JTextArea();
            detailsArea.setEditable(false);
            detailsArea.setText("Detailed information about " + teamName + "\n");
            // Add more details as needed
            
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            
            newPanel.add(scrollPane, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> newPanel.setVisible(false));
            newPanel.add(closeButton, BorderLayout.SOUTH);

            mainFrame.add(newPanel);
            activePanels.put(panelId, newPanel);
            newPanel.setVisible(true);
            newPanel.revalidate();
            newPanel.repaint();
        } else {
            panel.setVisible(true);
            panel.revalidate();
            panel.repaint();
        }
    }

    public void createOrUpdateTextTablePanel(String panelId, String title, String[][] data, String[] columnNames) {
        createOrUpdateTextTablePanel(panelId, title, "", data, columnNames);
    }

    public void createOrUpdateTextPanel(String panelId, String title) {
        createOrUpdateTextPanel(panelId, title, "");
    }

    public void createOrUpdateTextPanel(String panelId, String title, String message) {
        JPanel panel = activePanels.get(panelId);

        if (panel == null) {
            // Create new frame if it doesn't exist
            panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setPreferredSize(DEFAULT_DIMENSION);

            JLabel titleLabel = new JLabel(title);
            panel.add(titleLabel, BorderLayout.NORTH);

            if (!message.isEmpty()) {
                JTextArea messageArea = new JTextArea(message);
                messageArea.setEditable(false); // Make it read-only
                
                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setPreferredSize(new Dimension(500,300));
                
                panel.add(scrollPane, BorderLayout.CENTER);
            }
            
            JButton closePanelButton = new JButton("Close " + panelId);
            closePanelButton.addActionListener(e -> {
                activePanels.get(panelId).setVisible(false);
            });

            // Create a resize handle
            JPanel resizeHandle = new JPanel();
            resizeHandle.setPreferredSize(new Dimension(10, 10));
            resizeHandle.setBackground(Color.GRAY);
            resizeHandle.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            resizeHandle.setFocusable(true);

            final Point[] initialClick = new Point[1];
            resizeHandle.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    initialClick[0] = e.getPoint();
                }
            });
            resizeHandle.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    JPanel panel = activePanels.get(panelId);
                    final double sensitivity = 0.1;
                    
                    int newWidth = panel.getWidth() + (int) ((e.getX() - initialClick[0].x) * sensitivity);
                    int newHeight = panel.getHeight() + (int) ((e.getY() - initialClick[0].y) * sensitivity);
                    
                    newWidth = Math.max(newWidth, 100);
                    newHeight = Math.max(newHeight, 100);
                    newWidth = Math.min(newWidth, 400);
                    newHeight = Math.min(newHeight, 400);
                    
                    panel.setPreferredSize(new Dimension(newWidth, newHeight));
                    panel.revalidate();
                    panel.repaint();
                }
            });

            panel.add(resizeHandle, BorderLayout.EAST);
            panel.add(closePanelButton, BorderLayout.SOUTH);
            mainFrame.add(panel);
            activePanels.put(panelId, panel);
        } else {
            // Update existing panel
            // First, remove any existing message area
            for (int i = 0; i < panel.getComponentCount(); i++) {
                if (panel.getComponent(i) instanceof JScrollPane) {
                    panel.remove(i);
                    break;
                }
            }

            // Add new message area if there's a message
            if (!message.isEmpty()) {
                JTextArea messageArea = new JTextArea(message);
                messageArea.setEditable(false);
                
                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setPreferredSize(new Dimension(500,300));
                
                panel.add(scrollPane, BorderLayout.CENTER);
            }
        }

        // Show frame if not visible
        if (!panel.isVisible()) {
            panel.setVisible(true);
            panel.setPreferredSize(DEFAULT_DIMENSION);
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
        frame.setSize(300, 400);
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


        frame.setSize(700,500);
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

    public String toString() {
        return "GameUIGenerator{" +
                "activePanels=" + activePanels +
                ", activeFrames=" + activeFrames +
                ", activeLabels=" + activeLabels +
                ", mainFrame=" + mainFrame +
                '}';
    }
}
