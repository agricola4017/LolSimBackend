package GameObjects.TeamsAndPlayers;
import java.io.Serializable;

public class Player implements Serializable {
    private int playerID;
    private String playerName;
    private int teamID;
    private int age;
    private Position position;
    private float value;
    private Stat stat;
    private int OVR;
    private Region region;

    //test val
    public static int counter = 0;

    int kills = 0;
    int deaths = 0;
    int cs = 0;
    int gold = 0;
    int gamesPlayed = 0;

    //generate random player
    public Player() {
        this.playerID = counter; //grab latest id
        this.playerName = "test" + this.playerID; //generate name
        this.teamID = -1;
        this.age = 17;
        this.position = Position.top; //randomize
        this.value = 165.55f;
        this.stat = new Stat(); //generate
        this.region = Region.NA; //randomize
        this.OVR = stat.getOVR();
        counter++;
        
    }

    public Player(int playerID, String playerName, int teamID, int age, Position position, float value, Stat stat, Region region) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.teamID = teamID;
        this.age = age;
        this.position = position;
        this.value = value;
        this.stat = stat;
        this.region = region;
        this.OVR = stat.getOVR();
        counter++;
    }

    public static Player generatePlayer() {
        int playerID = counter; //grab latest id
        String playerName = "test" + playerID; //generate name
        int teamID = -1;
        int age = 17;
        Position position = Position.generateRandomPosition();
        float value = 65.00f;
        Stat stat = Stat.generateRandomStats();
        Region region = Region.generateRandomRegion();
        counter++;

        return new Player(playerID, playerName, teamID, age, position, value, stat, region);
    }

    public static Player generateNamedPlayerFromOVRandPosition(String name, int OVR, int teamID, Position position) {
        int playerID = counter; //grab latest id
        String playerName = name;
        int age = 17;
        float value = 65.00f;
        Stat stat = Stat.generateRandomStats(OVR);
        Region region = Region.generateRandomRegion();
        counter++;

        return new Player(playerID, playerName, teamID, age, position, value, stat, region);
    }

    public static Player generatePlayerWithTeam(int teamId) {
        int playerID = counter; //grab latest id
        String playerName = "test" + playerID; //generate name
        int age = 17;
        Position position = Position.generateRandomPosition();
        float value = 65.00f;
        Stat stat = Stat.generateRandomStats();
        Region region = Region.generateRandomRegion();
        counter++;

        return new Player(playerID, playerName, teamId, age, position, value, stat, region);
    }

    public static Player generatePerfectPlayer(int teamId) {
        int playerID = counter; //grab latest id
        String playerName = "test" + playerID; //generate name
        int age = 17;
        Position position = Position.generateRandomPosition();
        float value = 65.00f;
        Stat stat = Stat.generatePerfectStat();
        Region region = Region.generateRandomRegion();
        counter++;

        return new Player(playerID, playerName, teamId, age, position, value, stat, region);
    }

    public void updateSeasonStats(int kills, int deaths, int cs, int gold) {
        this.kills += kills;
        this.deaths += deaths;
        this.cs += cs;
        this.gold += gold;
        gamesPlayed++;
    }

    public void resetSeasonStats() {
        this.kills = 0;
        this.deaths = 0;
        this.cs = 0;
        this.gold = 0;
        gamesPlayed = 0;
    }

    public int[] getSeasonStats() {
        if (gamesPlayed == 0) 
            return new int[] {0, 0, 0, 0};
        else 
            return new int[] {this.kills/gamesPlayed, this.deaths/gamesPlayed, this.cs/gamesPlayed, this.gold/gamesPlayed};
    }

    public void increaseStats() {
        this.stat.increaseStats();
    }

    public void decreaseStats() { 
        this.stat.decreaseStats(); 
    } //decreaseStats

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getOVR() {
        return stat.getOVR();
    }

    @Override
    public String toString() {
        int gp = gamesPlayed;
        if (gamesPlayed == 0) { 
            gp = 1;
        }
        return "playerID=" + playerID +  
                ", playerName='" + playerName + '\'' +
                ", age=" + age +
                ", teamID=" + teamID +
                ", position=" + position +
                //", value=" + value +
                //", stat=" + stat +
                //", region=" + region +
                ", OVR= " + stat.getOVR() + 
                ", KDA=" + kills/gp + "/" + deaths/gp +
                ", CS=" + cs/gp + " " + ",Gold=" + gold/gp;
    }
}
