package GameObjects.Game.Callbacks;

import GameObjects.TeamsAndPlayers.Position;

@FunctionalInterface
public interface PlayerFormCallback {
    void onPlayerSubmit(String name, Position position, int ovr);
}