package Database;

import GameObjects.TeamsAndPlayers.Player;

import java.util.List;

class Main {

    static Repository repository;

    public static void main(String[] args)
    {
        repository = Repository.getRepository();

        testAddPlayer();
        testGetPlayers();
    }

    public static void testAddPlayer() {
        Player player = new Player();

        //System.out.println(players);

        repository.addPlayer(player);
    }

    public static List<Player> testGetPlayers() {
        List<Player> players = repository.getActivePlayers();
        System.out.println(players);
        return players;
    }
}