package Database;
import GameObjects.TeamsAndPlayers.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Repository {

    private static Repository db;
    private Repository() {
    }

    static Repository getRepository() {
        if (db == null) {
            db = new Repository();
        }
        return db;
    }
    private Connection connect() throws SQLException {
        try {
            // Get database credentials from DatabaseConfig class
            String jdbcUrl = DatabaseConfig.getDbUrl();
            String user = DatabaseConfig.getDbUsername();
            String password = DatabaseConfig.getDbPassword();

            // Open a connection
            return DriverManager.getConnection(jdbcUrl, user, password);

        } catch (SQLException  e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    List<Team> getActiveTeams() {
        List<Team> teams = new ArrayList<>();

        String sql = "SELECT * from teams t";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String teamName = rs.getString("playername");
                int teamId = rs.getInt("teamid");

                Team team = new Team(teamId, teamName);
                teams.add(team);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return teams;
    }

    List<Player> getActivePlayers() {
        List<Player> players = new ArrayList<Player>();

        String sql = "SELECT * from players p where p.teamid != -1";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int playerId = rs.getInt("playerid");
                String playerName = rs.getString("playername");
                int teamId = rs.getInt("teamid");
                int age = rs.getInt("age");
                String position = rs.getString("position");
                float value = rs.getFloat("value");
                int laning = rs.getInt("laning");
                int teamfighting = rs.getInt("teamfighting");
                int economy = rs.getInt("economy");
                int consistency = rs.getInt("consistency");
                int teamwork = rs.getInt("teamwork");
                int aggression = rs.getInt("aggression");
                int stamina = rs.getInt("stamina");
                int potential = rs.getInt("potential");
                String region = rs.getString("region");

                Stat stat = new Stat(laning, teamfighting, economy, consistency, teamwork, aggression, stamina, potential);
                Player player = new Player(playerId, playerName, teamId, age, Position.valueOf(position), value, stat, Region.valueOf(region));

                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    int addPlayers(List<Player> players) {
        for (Player player: players) {
            addPlayer((player));
        }
        return -1;
    }
    int addPlayer(Player player) {

        //logic to parse if teamid is in db

        String sql = "INSERT INTO players(playername,teamid,age,position,value,laning,teamfighting," +
                "economy,consistency,teamwork,aggression,stamina,potential,region) VALUES(?, ?, ?, ?::role," +
                " ?, ?, ?, ?, ? , ? , ?, ?, ?, ?::region)";

        //System.out.println(sql);

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, player.getPlayerName());
            stmt.setInt(2, player.getTeamID());
            stmt.setInt(3, player.getAge());
            stmt.setObject(4, player.getPosition().toString());
            stmt.setDouble(5, player.getValue());
            stmt.setInt(6, player.getStat().getLaning());
            stmt.setInt(7, player.getStat().getTeamfighting());
            stmt.setInt(8, player.getStat().getEconomy());
            stmt.setInt(9, player.getStat().getConsistency());
            stmt.setInt(10, player.getStat().getTeamwork());
            stmt.setInt(11, player.getStat().getAggression());
            stmt.setInt(12, player.getStat().getStamina());
            stmt.setInt(13, player.getStat().getPotential());
            stmt.setObject(14, player.getRegion().toString());

            int insertedRow = stmt.executeUpdate();
            if (insertedRow > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


}