package GameObjects.TeamsAndPlayers;

import static Functions.GenerateFunctions.generateStatRangeInclusive;

public enum Position {
    top,
    jgl,
    mid,
    adc,
    spt;

    private static Position[] positions = Position.values();

    public static Position generateRandomPosition() {
        return positions[generateStatRangeInclusive(0, positions.length - 1)];
    }

    public static Position[] getPositions() {
        return positions;
    }
}
