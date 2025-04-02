package Game.GameUI;

/**
 * Helper class to hold table data and column names
 */
public class TableData {
    public final String[][] data;
    public final String[] columnNames;

    public TableData(String[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }
} 