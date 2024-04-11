package GameObjects.TeamsAndPlayers;

import static Functions.GenerateFunctions.generateStatRangeInclusive;

public enum Region {
    NA,
    EU,
    KR,
    CN,
    VN,
    JP,
    MEX,
    AF,
    BR,
    TW;

    private static Region[] regions = Region.values();

    public static Region generateRandomRegion() {
        return regions[generateStatRangeInclusive(0, regions.length - 1)];
    }

    public static Region[] getRegions() {
        return regions;
    }


}
