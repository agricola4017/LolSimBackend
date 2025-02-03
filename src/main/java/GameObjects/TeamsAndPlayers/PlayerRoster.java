package GameObjects.TeamsAndPlayers;

import java.util.*;
import java.io.Serializable;

public class PlayerRoster implements Serializable {

    //restrict to one roster per team
    private EnumMap<Position,Player> activePlayers;

    public PlayerRoster() {
        this.activePlayers = new EnumMap<>(Position.class);
    }

    public PlayerRoster(List<Player> players) {
        this.activePlayers = new EnumMap<>(Position.class);

        normalizePlayers(players);

    }

    public void normalizePlayers(List<Player> players) {
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

    public int getOVR() {
        int agg = 0;
        for (Player p: activePlayers.values()) {
            agg += p.getStat().getOVR();
        }
        return (int) Math.round(agg/5);
    }
    @Override
    public String toString() {
        String ret = "PlayerRoster: \n";
        ret += "------------------------\n";
        ret += "Position | " + Player.toStringHeaders() + "\n";
        for (Position p : Position.values()) {
            ret += p + " | " + activePlayers.get(p) + "\n";
        }
        ret += "------------------------\n";
        return ret;
    }
}
