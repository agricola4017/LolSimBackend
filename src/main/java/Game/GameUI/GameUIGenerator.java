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
import javax.swing.JSlider;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import Game.Callbacks.IDFormCallback;
import Game.Callbacks.PlayerFormCallback;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

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
import java.util.Hashtable;
import java.awt.Component;
import javax.swing.table.DefaultTableCellRenderer;


public class GameUIGenerator {
    private JFrame mainFrame;
    private Map<String, JPanel> activePanels;
    private Map<String, JFrame> activeFrames;
    private Map<String, JLabel> activeLabels;
    private static final Dimension DEFAULT_DIMENSION = new Dimension(750, 450);
    private static final Dimension MESSAGE_SCROLL_DIMENSION = new Dimension(750, 45);
    private static final Dimension MAIN_FRAME_DIMENSION = new Dimension(1800, 1200);
    private static final Dimension MIN_PANEL_DIMENSION = new Dimension(150, 150);
    private static final Dimension MAX_PANEL_DIMENSION = new Dimension(1200, 800);
    private static final Dimension RESIZE_HANDLE_DIMENSION = new Dimension(15, 15);
    private static final String[] clickableColumns = {"Team", "Winner", "Runner Up"};

    public GameUIGenerator() {
        mainFrame = new JFrame("Game UI");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(MAIN_FRAME_DIMENSION);
        mainFrame.setLayout(new FlowLayout());
        activePanels = new HashMap<>();
        activeFrames = new HashMap<>();
        activeLabels = new HashMap<>();

        mainFrame.setVisible(true);
    }

    public void createOrUpdateTextTablePanel(String panelId, String title, String message, String[][] data, String[] columnNames) {
        // Create a map with a single table using the panel title as the table name
        Map<String, TableData> tables = new HashMap<>();
        tables.put(title, new TableData(data, columnNames));
        
        // Use the multi-table panel method
        createOrUpdateTextMultiTablePanel(panelId, title, message, tables);
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
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            panel.add(titleLabel, BorderLayout.NORTH);

            if (!message.isEmpty()) {
                JTextArea messageArea = new JTextArea(message);
                messageArea.setEditable(false);
                messageArea.setBackground(Color.WHITE);
                messageArea.setForeground(new Color(51, 51, 51));
                messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                
                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setPreferredSize(new Dimension(750, 450));
                scrollPane.setBorder(null);
                
                panel.add(scrollPane, BorderLayout.CENTER);
            }
            
            JButton closePanelButton = new JButton("Close Panel");
            closePanelButton.setFont(new Font("Arial", Font.PLAIN, 14));
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
                messageArea.setBackground(Color.WHITE);
                messageArea.setForeground(new Color(51, 51, 51));
                messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
                messageArea.setLineWrap(true);
                messageArea.setWrapStyleWord(true);
                messageArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                
                JScrollPane scrollPane = new JScrollPane(messageArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setPreferredSize(new Dimension(750, 450));
                
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

    public void createOrUpdateTextMultiTablePanel(String panelId, String title, String message, Map<String, TableData> tables) {
        final JPanel panel = activePanels.get(panelId);

        // Create new panel if it doesn't exist
        final JPanel newPanel = panel == null ? new JPanel() : panel;
        newPanel.setLayout(new BorderLayout());
        newPanel.setPreferredSize(DEFAULT_DIMENSION);
        newPanel.setBorder(null); // Remove panel border

        // Update title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Remove existing components
        newPanel.removeAll();
        newPanel.add(titleLabel, BorderLayout.NORTH);

        // Create main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Create button panel with no border
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(null);

        // Create content panel with no border
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(null);

        // Create table panel with no border
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(null);

        // Create tables and buttons in the new order
        Map<String, JTable> tableMap = new HashMap<>();
        
        // Determine which table should be shown by default
        String defaultTableName = null;
        if (tables.size() == 1) {
            // For single table panels, show the only table
            defaultTableName = tables.keySet().iterator().next();
        } else if (title.equals("Match Log")) {
            // For Match Log, show MatchSummary by default
            defaultTableName = "Match Summary";
        } else {
            // For other multi-table panels, show Starting Roster by default
            for (String key : tables.keySet()) {
                if (key.equals("Starting Roster")) {
                    defaultTableName = "Starting Roster";
                    break;
                }
            }
        }
        
        // Create a new ordered map with the default table first
        Map<String, TableData> orderedTables = new LinkedHashMap<>();
        
        // For Match Log panels, ensure MatchSummary is first
        if (defaultTableName != null) {
            TableData defaultTableData = tables.get(defaultTableName);
            if (defaultTableData != null) {
                orderedTables.put(defaultTableName, defaultTableData);
            }
        }
        
        // Add remaining tables
        for (Map.Entry<String, TableData> entry : tables.entrySet()) {
            String key = entry.getKey();
            TableData tableData = entry.getValue();
            if (tableData != null && !key.equals(defaultTableName) && 
                !(title.equals("Match Log") && key.equals("Match Summary"))) {
                orderedTables.put(key, tableData);
            }
        }

        // Create tables and buttons in the new order
        for (Map.Entry<String, TableData> entry : orderedTables.entrySet()) {
            String tableName = entry.getKey();
            TableData tableData = entry.getValue();
            
            // Skip empty tables
            if (tableData == null || tableData.data == null || tableData.columnNames == null || 
                tableData.data.length == 0 || tableData.columnNames.length == 0) {
                continue;
            }
            
            // Create table
            JTable table = createTable(tableData.data, tableData.columnNames);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            tableMap.put(tableName, table);
            
            // Create toggle button
            JButton toggleButton = new JButton(tableName);
            toggleButton.setFont(new Font("Arial", Font.PLAIN, 14));
            toggleButton.setSelected(false);
            buttonPanel.add(toggleButton);
            
            // Add action listener to toggle button
            toggleButton.addActionListener(e -> {
                // Clear the table panel
                tablePanel.removeAll();
                
                // Create scroll pane for the selected table
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setBorder(null);
                
                // Add the selected table to the panel
                tablePanel.add(scrollPane, BorderLayout.CENTER);
                
                // Update button states
                for (Component c : buttonPanel.getComponents()) {
                    if (c instanceof JButton) {
                        c.setBackground(Color.WHITE);
                    }
                }
                toggleButton.setBackground(new Color(200, 200, 200));
                
                // Revalidate and repaint
                tablePanel.revalidate();
                tablePanel.repaint();
            });
        }

        // Set default table visibility
        for (Map.Entry<String, JTable> entry : tableMap.entrySet()) {
            if (entry.getKey().equals(defaultTableName)) {
                JScrollPane scrollPane = new JScrollPane(entry.getValue());
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setBorder(null);
                tablePanel.add(scrollPane, BorderLayout.CENTER);
                tablePanel.revalidate();
                tablePanel.repaint();
            }
        }

        // Add table panel to content panel
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Add button panel and content panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add close button
        JButton closePanelButton = new JButton("Close Panel");
        closePanelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closePanelButton.addActionListener(e -> {
            newPanel.setVisible(false);
        });

        // Create a popup menu for resize options
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Create a panel for the slider
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 5));
        sliderPanel.setBackground(Color.WHITE);
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create a single slider for diagonal scaling
        JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, 
            0, // Minimum index
            5, // Maximum index (6 options total)
            2); // Default index (100% scale)
        scaleSlider.setMajorTickSpacing(1);
        scaleSlider.setMinorTickSpacing(1);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.setSnapToTicks(true);
        
        // Create custom labels for the slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("50%"));
        labelTable.put(1, new JLabel("75%"));
        labelTable.put(2, new JLabel("100%"));
        labelTable.put(3, new JLabel("125%"));
        labelTable.put(4, new JLabel("150%"));
        labelTable.put(5, new JLabel("175%"));
        scaleSlider.setLabelTable(labelTable);
        
        // Add label to show current scale
        JLabel scaleLabel = new JLabel("Scale: 100%");
        scaleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        sliderPanel.add(scaleLabel, BorderLayout.NORTH);
        sliderPanel.add(scaleSlider, BorderLayout.CENTER);
        
        // Add change listener to slider
        scaleSlider.addChangeListener(e -> {
            int index = scaleSlider.getValue();
            int scale;
            switch (index) {
                case 0: scale = 50; break;
                case 1: scale = 75; break;
                case 2: scale = 100; break;
                case 3: scale = 125; break;
                case 4: scale = 150; break;
                case 5: scale = 175; break;
                default: scale = 100;
            }
            scaleLabel.setText("Scale: " + scale + "%");
            
            // Calculate new dimensions maintaining aspect ratio
            int newWidth = (int) (DEFAULT_DIMENSION.width * scale / 100.0);
            int newHeight = (int) (DEFAULT_DIMENSION.height * scale / 100.0);
            
            // Constrain to min/max dimensions
            newWidth = Math.min(Math.max(newWidth, MIN_PANEL_DIMENSION.width), MAX_PANEL_DIMENSION.width);
            newHeight = Math.min(Math.max(newHeight, MIN_PANEL_DIMENSION.height), MAX_PANEL_DIMENSION.height);
            
            newPanel.setPreferredSize(new Dimension(newWidth, newHeight));
            newPanel.revalidate();
            newPanel.repaint();
        });
        
        // Add slider panel directly to popup menu
        popupMenu.add(sliderPanel);
        
        // Add mouse listener for popup to the entire panel and all its components
        newPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        // Add the same mouse listener to all components in the panel
        for (java.awt.Component component : newPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel subPanel = (JPanel) component;
                subPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                    
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
            }
        }
        
        // Add tooltip to indicate right-click functionality
        newPanel.setToolTipText("Right-click to resize panel");

        // Add components to panel
        newPanel.add(closePanelButton, BorderLayout.SOUTH);
        newPanel.add(mainPanel, BorderLayout.CENTER);

        // Add to main frame if this is a new panel
        if (panel == null) {
            mainFrame.add(newPanel);
            activePanels.put(panelId, newPanel);
        }

        // Show panel and update
        newPanel.setVisible(true);
        newPanel.revalidate();
        newPanel.repaint();
    }

    // Helper method to create a table with common settings
    private JTable createTable(String[][] data, String[] columnNames) {
        JTable table = new JTable(data, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(25);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Get the column name
                String columnName = table.getColumnModel().getColumn(column).getHeaderValue().toString();

                // Check if this is a clickable cell (team name columns)
                boolean teamColumn = false;
                for (String clickableColumn : clickableColumns) {
                    if (columnName.equals(clickableColumn)) {
                        teamColumn = true;
                        break;
                    }
                }
                boolean isClickable = teamColumn && value != null && !value.toString().isEmpty();
                
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

                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                if (isSelected) {
                    c.setBackground(new Color(230, 230, 230));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return c;
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                
                if (row >= 0 && col >= 0) {
                    // Get the column value
                    String cellValue = table.getValueAt(row, col).toString();
                    String cellColumn = table.getColumnModel().getColumn(col).getHeaderValue().toString();
                    
                    // Only handle clicks on team name columns

                    // Check if this is a clickable cell (team name columns)
                    boolean teamColumn = false;
                    for (String clickableColumn : clickableColumns) {
                        if (cellColumn.equals(clickableColumn)) {
                            teamColumn = true;
                            break;
                        }
                    }
             
                    if (teamColumn && cellValue != null)  {
                        // Convert row index to model index for sorted tables
                        int modelRow = table.convertRowIndexToModel(row);
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
        return table;
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
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            newPanel.add(titleLabel, BorderLayout.NORTH);

            // Create a container panel for message and tables
            JPanel contentPanel = new JPanel(new BorderLayout());
            newPanel.add(contentPanel, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close Panel");
            closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
            closeButton.addActionListener(e -> newPanel.setVisible(false));
            newPanel.add(closeButton, BorderLayout.SOUTH);

            mainFrame.add(newPanel);
            activePanels.put(panelId, newPanel);
            newPanel.setVisible(true);
            newPanel.revalidate();
            newPanel.repaint();

            // Notify the callback that a new panel is ready
            if (teamDetailsCallback != null) {
                teamDetailsCallback.onTeamDetailsPanelReady(panelId, teamName);
            }
        } else {
            panel.setVisible(true);
            panel.revalidate();
            panel.repaint();
        }
    }

    // Add callback interface for team details
    public interface TeamDetailsCallback {
        void onTeamDetailsPanelReady(String panelId, String teamName);
    }

    private TeamDetailsCallback teamDetailsCallback;

    public void setTeamDetailsCallback(TeamDetailsCallback callback) {
        this.teamDetailsCallback = callback;
    }
}

