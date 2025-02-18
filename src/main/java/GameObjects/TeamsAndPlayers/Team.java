package GameObjects.TeamsAndPlayers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Serializable;

public class Team implements Serializable { 
    private int teamID;
    private String teamName;
    private List<Player> players;
    private PlayerRoster playerRoster;

    private int wins;
    private int standing;
    private int avgPlacement;

    public Team(int teamID, String teamName) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.players = new ArrayList<>();
        this.playerRoster = new PlayerRoster();

        this.wins=0;
        this.standing=0;
        this.avgPlacement=0;
        
    }

    public Team(int teamID, String teamName, List<Player> players) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.players = players;
        this.playerRoster = new PlayerRoster(players);

        this.wins=0;
        this.standing=0;
        this.avgPlacement=0;
    }

    public void addPlacement(int placement, int seasonsPlayed) {
        this.avgPlacement = (this.avgPlacement * (seasonsPlayed - 1) + placement) / seasonsPlayed;
    }

    public void normalizePlayers() {
        this.playerRoster.normalizePlayers(this.players);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getNonRosterPlayers() {
        List<Player> nonRosterPlayers = new LinkedList<>(this.players);
        nonRosterPlayers.removeAll(this.playerRoster.getActivePlayers().values());
        return nonRosterPlayers;
    }

    public void setPlayers(List<Player> players) {
        for (Player player : players) {
            player.setTeamID(this.teamID);
        }
        this.players = players;
    }

    public void addPlayer(Player player) {
        player.setTeamID(this.teamID);
        this.players.add(player);
    }

    public void addPlayers(List<Player> players) {
        for (Player player : players) {
            player.setTeamID(this.teamID);
            this.players.add(player);
        }
    }

    public void removePlayer(Player player) {
        player.setTeamID(-1);
        this.players.remove(player);
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public PlayerRoster getPlayerRoster() {
        return playerRoster;
    }

    public void setPlayerRoster(PlayerRoster playerRoster) {
        this.playerRoster = playerRoster;
    }

    public int addWin() {
        this.wins += 1;
        return this.wins;
    }

    public void setStanding(int pos) {
        this.standing = pos;
    }

    @Override
    public String toString() {
        String ret = "";

        ret += "Team Name=" + teamName + "\n" +
                "Wins=" + wins + "\n" +
                "Average Placement=" + avgPlacement + "\n" +
                "OVR=" + playerRoster.getOVR() + "\n" +
                //", players=" + players +
                "Roster=" + playerRoster;

        Set<Player> parsedPlayers = new HashSet<>(playerRoster.getActivePlayers().values());

        for (Player p : players) {
            if (parsedPlayers.contains(p)) {
                continue;
            } else {
                ret += p + "\n";
                parsedPlayers.add(p);
            }
        }
        return ret;
    }
}
