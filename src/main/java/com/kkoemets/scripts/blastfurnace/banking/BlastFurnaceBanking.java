package com.kkoemets.scripts.blastfurnace.banking;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.location.Area.Polygonal;
import static com.runemate.game.api.hybrid.location.Area.polygonal;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BlastFurnaceBanking {
    private static final Coordinate BANKING_TILE = new Coordinate(1948, 4957, 0);
    private static final Polygonal areNearBank = getPolygonal(BANKING_TILE, 5);

    private BlastFurnaceBanking() {
    }

    public static boolean goToBank(BotLogger log) {
        if (getLocal().getAnimationId() != -1) {
            log.debug("Walking to bank...");
            return goToBank(log);
        }

        if (isAtBankLocation()) {
            log.debug("Player is near bank!");
            return true;
        }

        boolean isClickOnBankAreaSuccess = areNearBank.click();
        if (!isClickOnBankAreaSuccess) {
            return goToBank(log);
        }

        delay(1563, 2345);
        return goToBank(log);
    }

    private static boolean isAtBankLocation() {
        return areNearBank.contains(getLocal().getPosition());
    }

    private static Polygonal getPolygonal(Coordinate coordinate, int distanceFromCenter) {
        return polygonal(
                new Coordinate(coordinate.getX() - distanceFromCenter,
                        coordinate.getY() - distanceFromCenter, 0),
                new Coordinate(coordinate.getX() - distanceFromCenter,
                        coordinate.getY() + distanceFromCenter, 0),
                new Coordinate(coordinate.getX() + distanceFromCenter,
                        coordinate.getY() + distanceFromCenter, 0),
                new Coordinate(coordinate.getX() + distanceFromCenter,
                        coordinate.getY() - distanceFromCenter, 0));
    }

}