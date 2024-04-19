package GameObjects.TeamsAndPlayers;

import static Functions.Functions.randomNumberCustom;

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

    private final static Region[] regions = Region.values();

    public static Region generateRandomRegion() {
        return regions[randomNumberCustom(0, regions.length - 1)];
    }

    public static Region[] getRegions() {
        return regions;
    }


}
