package testPlayground;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import GameObjects.TeamsAndPlayers.Player;
import GameObjects.TeamsAndPlayers.PlayerRoster;
import GameObjects.TeamsAndPlayers.Team;

public class testSerializable {
    public static void main(String[] args) {
        // Create a list of players for testing
        List<Player> players = new ArrayList<>();
        players.add(new Player()); // Add some players
        players.add(new Player());

        // Create a PlayerRoster
        PlayerRoster roster = new PlayerRoster(players);

        // Create a Team
        Team team = new Team(1, "Team A", players);

        try {
            // Serialize Player
            FileOutputStream playerOut = new FileOutputStream("player.ser");
            ObjectOutputStream playerOOS = new ObjectOutputStream(playerOut);
            playerOOS.writeObject(players.get(0)); // Serialize the first player
            playerOOS.close();
            playerOut.close();

            // Serialize PlayerRoster
            FileOutputStream rosterOut = new FileOutputStream("roster.ser");
            ObjectOutputStream rosterOOS = new ObjectOutputStream(rosterOut);
            rosterOOS.writeObject(roster);
            rosterOOS.close();
            rosterOut.close();

            // Serialize Team
            FileOutputStream teamOut = new FileOutputStream("team.ser");
            ObjectOutputStream teamOOS = new ObjectOutputStream(teamOut);
            teamOOS.writeObject(team);
            teamOOS.close();
            teamOut.close();

            // Deserialize Player
            FileInputStream playerIn = new FileInputStream("player.ser");
            ObjectInputStream playerOIS = new ObjectInputStream(playerIn);
            Player deserializedPlayer = (Player) playerOIS.readObject();
            playerOIS.close();
            playerIn.close();

            // Deserialize PlayerRoster
            FileInputStream rosterIn = new FileInputStream("roster.ser");
            ObjectInputStream rosterOIS = new ObjectInputStream(rosterIn);
            PlayerRoster deserializedRoster = (PlayerRoster) rosterOIS.readObject();
            rosterOIS.close();
            rosterIn.close();

            // Deserialize Team
            FileInputStream teamIn = new FileInputStream("team.ser");
            ObjectInputStream teamOIS = new ObjectInputStream(teamIn);
            Team deserializedTeam = (Team) teamOIS.readObject();
            teamOIS.close();
            teamIn.close();

            // Output results
            System.out.println("Deserialized Player: " + deserializedPlayer);
            System.out.println("Deserialized PlayerRoster: " + deserializedRoster);
            System.out.println("Deserialized Team: " + deserializedTeam);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
