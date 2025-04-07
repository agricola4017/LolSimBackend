package GameObjects.TeamsAndPlayers;

import java.util.*;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;

public class PlayerRoster implements Serializable {

    //restrict to one roster per team
    private EnumMap<Position,Player> activePlayers;
    private List<SimpleEntry<Position, Integer>> weakestToStrongestPositions;

    public PlayerRoster() {
        this.activePlayers = new EnumMap<>(Position.class);
        this.weakestToStrongestPositions = new LinkedList<SimpleEntry<Position, Integer>>();
    }

    public PlayerRoster(List<Player> players) {
        this.activePlayers = new EnumMap<>(Position.class);
        this.weakestToStrongestPositions = new LinkedList<SimpleEntry<Position, Integer>>();
        normalizePlayers(players);
    }

    /**
     * normalizes the player roster by sorting the players by position and adding them to the active players map
     * Fills weakestToStrongestPositions
     * adds Players 'YearsStartingWithTeam' count 
     * @param players
     */
    public void normalizePlayers(List<Player> players) {

        EnumMap<Position,Player> oldPlayers = this.activePlayers;
        // Clear current active players
        this.activePlayers.clear();
        
        // Create a list of available players (to avoid duplicates)
        List<Player> availablePlayers = new ArrayList<>(players);
        // For each position, try to find the best player
        Queue<Position> positions = new LinkedList<Position>();
        positions.addAll(Arrays.asList(Position.values()));
        int count = 0;
        while (!positions.isEmpty()) {
            Position position = positions.poll();
            Player bestPlayer = null;
            int highestOVR = -1;
            
            // First try to find the best player for this specific position
            for (Player player : availablePlayers) {
                if (player.getPosition() == position && player.getStat().getOVR() > highestOVR) {
                    bestPlayer = player;
                    highestOVR = player.getStat().getOVR();
                }
            }
            
            // If no player found for this position, get the highest OVR available player
            if (bestPlayer == null) {
                if (count < 5) {
                    positions.add(position);
                } else {
                for (Player player : availablePlayers) {
                    if (player.getStat().getOVR() > highestOVR) {
                        bestPlayer = player;
                        highestOVR = player.getStat().getOVR();
                    }
                }
            }
            }
            
            // If we found a player, add them to active roster and remove from available pool
            if (bestPlayer != null) {
                this.activePlayers.put(position, bestPlayer);
                availablePlayers.remove(bestPlayer);
            }

            count++;
        }

        for (Position position : Position.getPositions()) {
            SimpleEntry<Position, Integer> entry = new SimpleEntry<>(position, activePlayers.get(position).getStat().getOVR());
            weakestToStrongestPositions.add(entry);
        }
        
        weakestToStrongestPositions.sort((a, b) -> b.getValue() - a.getValue());

        for (Position position : Position.getPositions()) {
            if (oldPlayers.get(position) != this.activePlayers.get(position)) {
                oldPlayers.get(position).resetYearsStartingWithTeam();
            }
        }
    }

    public Map<Position,Player> getActivePlayers() {
        return activePlayers;
    }

    public List<Player> getRosterAsList() {
        return new ArrayList<>(activePlayers.values());
    }
    public void setActivePlayers(EnumMap<Position,Player> activePlayers) {
        this.activePlayers = activePlayers;
    }

    public Player getPlayerByPosition(Position position) {
        return activePlayers.get(position);
    }

    public int getOVR() {
        int agg = 0;
        for (Player p: activePlayers.values()) {
            agg += p.getStat().getOVR();
        }
        return (int) Math.round(agg/5);
    }
    public String[] getRosterColumnNames() {
        String[] playerColumns = Player.getPlayerColumnNames();
        String[] rosterColumns = new String[playerColumns.length + 1];
        rosterColumns[0] = "Position";
        System.arraycopy(playerColumns, 0, rosterColumns, 1, playerColumns.length);
        return rosterColumns;
    }

    public String[][] getRosterData() {
        String[][] data = new String[Position.values().length][Player.getPlayerColumnNames().length + 1];
        int row = 0;
        for (Position p : Position.values()) {
            Player player = activePlayers.get(p);
            data[row][0] = p.toString();
            if (player != null) {
                String[] playerData = player.getPlayerData();
                System.arraycopy(playerData, 0, data[row], 1, playerData.length);
            } else {
                // Fill empty position with dashes
                for (int i = 1; i < data[row].length; i++) {
                    data[row][i] = "-";
                }
            }
            row++;
        }
        return data;
    }

    @Override
    public String toString() {
        String ret = "PlayerRoster: \n";
        ret += "------------------------\n";
        ret += "Position | " + String.join(" | ", Player.getPlayerColumnNames()) + "\n";
        for (Position p : Position.values()) {
            ret += p + " | " + activePlayers.get(p) + "\n";
        }
        ret += "------------------------\n";
        return ret;
    }
}

