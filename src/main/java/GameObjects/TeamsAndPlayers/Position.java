package GameObjects.TeamsAndPlayers;


import static Functions.Functions.randomNumberCustom;

public enum Position {
    top,
    jgl,
    mid,
    adc,
    spt;

    private final static Position[] positions = Position.values();

    public static Position generateRandomPosition() {
        return positions[randomNumberCustom(0, positions.length - 1)];
    }

    public static Position[] getPositions() {
        return positions;
    }
}
