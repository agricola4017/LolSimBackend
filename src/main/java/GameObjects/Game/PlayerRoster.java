package GameObjects.Game;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.Position;

import java.util.*;

public class PlayerRoster {

    //restrict to one roster per team
    private Map<Position,Player> activePlayers;

    public PlayerRoster() {
        this.activePlayers = new HashMap<>();
    }

    public PlayerRoster(List<Player> players) {
        this.activePlayers = new HashMap<>();

        normalizePlayers(players);

    }

    public void normalizePlayers(List<Player> players) {
        int i = 0;
        for (Position p: Position.getPositions()) {
            this.activePlayers.put(p, players.get(i));
            i++;
        }
    }
    public Map<Position,Player> getActivePlayers() {
        return activePlayers;
    }

    public List<Player> getRosterAsList() {
        return new ArrayList<>(activePlayers.values());
    }
    public void setActivePlayers(Map<Position,Player> activePlayers) {
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
        return "PlayerRoster{" +
                "activePlayers=" + activePlayers +
                '}';
    }
}
