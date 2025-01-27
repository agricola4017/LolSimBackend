package GameObjects.TeamsAndPlayers;

public class Player {
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

    //generate random player
    public Player() {
        this.playerID = counter; //grab latest id
        this.playerName = "test" + this.playerID; //generate name
        this.teamID = 0;
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
        int teamID = 0;
        int age = 17;
        Position position = Position.generateRandomPosition();
        float value = 65.00f;
        Stat stat = Stat.generateRandomStats();
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

    @Override
    public String toString() {
        return "" +//"playerID=" + playerID +
                "playerName='" + playerName + '\'' +
                //", teamID=" + teamID +
                //", age=" + age +
                ", position=" + position +
                //", value=" + value +
                //", stat=" + stat +
                //", region=" + region +
                ", OVR= " + stat.getOVR();
    }
}
