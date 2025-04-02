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
        return new String[]{
            "Position",
            "Name",
            "OVR",
            "POT",
            "OVR Δ",
            "POT Δ",
            "KDA",
            "CS/Gold",
            "Age",
            "YWT",
            "YSWT",
            "Contract"
        };
    }

    public String[][] getRosterData() {
        String[][] data = new String[Position.values().length][12];
        int row = 0;
        for (Position p : Position.values()) {
            Player player = activePlayers.get(p);
            data[row][0] = p.toString();
            if (player != null) {
                int[] stats = player.getSeasonStats();
                data[row][1] = player.getPlayerName();
                data[row][2] = String.valueOf(player.getOVR());
                data[row][3] = String.valueOf(player.getStat().getPotential());
                data[row][4] = (player.getStat().getOVR() - player.getOVR() >= 0 ? "+" : "") + 
                              (player.getStat().getOVR() - player.getOVR());
                data[row][5] = (player.getStat().getPotential() - player.getStat().getPotential() >= 0 ? "+" : "") + 
                              (player.getStat().getPotential() - player.getStat().getPotential());
                data[row][6] = stats[0] + "/" + stats[1];
                data[row][7] = stats[2] + " / " + stats[3];
                data[row][8] = String.valueOf(player.getAge());
                data[row][9] = String.valueOf(player.getYearsWithTeam());
                data[row][10] = String.valueOf(player.getYearsStartingWithTeam());
                data[row][11] = String.valueOf(player.getValue());
            } else {
                data[row][1] = "Empty";
                data[row][2] = "-";
                data[row][3] = "-";
                data[row][4] = "-";
                data[row][5] = "-";
                data[row][6] = "-";
                data[row][7] = "-";
                data[row][8] = "-";
                data[row][9] = "-";
                data[row][10] = "-";
                data[row][11] = "-";
            }
            row++;
        }
        return data;
    }

    @Override
    public String toString() {
        String ret = "PlayerRoster: \n";
        ret += "------------------------\n";
        ret += "Position | " + Player.getColumnHeaders() + "\n";
        for (Position p : Position.values()) {
            ret += p + " | " + activePlayers.get(p) + "\n";
        }
        ret += "------------------------\n";
        return ret;
    }
}

