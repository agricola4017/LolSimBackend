package GameObjects.TeamsAndPlayers;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int teamID;
    private String teamName;
    private List<Player> players;
    private PlayerRoster playerRoster;

    public Team(int teamID, String teamName) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.players = new ArrayList<>();
        this.playerRoster = new PlayerRoster();
    }

    public Team(int teamID, String teamName, List<Player> players) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.players = players;
        this.playerRoster = new PlayerRoster(players);
    }


    public void normalizePlayers() {
        this.playerRoster.normalizePlayers(this.players);
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void addPlayers(List<Player> players) {
        for (Player player : players) {
            this.players.add(player);
        }
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

    @Override
    public String toString() {
        return "Team{" +
                "teamID=" + teamID +
                ", teamName='" + teamName + '\'' +
                //", players=" + players +
                ", playerRoster=" + playerRoster +
                '}';
    }
}
