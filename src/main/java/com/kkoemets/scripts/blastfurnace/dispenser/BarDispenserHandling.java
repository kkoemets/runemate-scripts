package com.kkoemets.scripts.blastfurnace.dispenser;

import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.ICE_GLOVES;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.isOpen;
import static com.runemate.game.api.hybrid.location.Area.polygonal;
import static com.runemate.game.api.hybrid.region.GameObjects.newQuery;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BarDispenserHandling {

    private static final Coordinate CONVEYOR_TILE = new Coordinate(1942, 4967, 0);
    private static final Area.Polygonal BAR_DISPENSER_AREA = polygonal(
            new Coordinate(1941 + 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4962 - 1, 0),
            new Coordinate(1941 + 1, 4962 - 1, 0));

    private BarDispenserHandling() {
    }

    public static boolean hasBarDispenserGoldBars() {
        return load(NamedVarbit.BLAST_FURNACE_GOLD_BAR.getId()).getValue() > 0;
    }

    public static boolean takeGoldBarsFromBarDispenser(BotLogger log) {
        if (!hasBarDispenserGoldBars() || Inventory.isFull()) {
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Player is moving...");
            delay(200, 300);
            return takeGoldBarsFromBarDispenser(log);
        }

        if (!Equipment.contains(ICE_GLOVES) && !Inventory.getItems(ICE_GLOVES).get(0).click()) {
            delay(400, 600);
            return takeGoldBarsFromBarDispenser(log);
        }

        if (isTakeGoldBarsFromDispenserDialogOpen() || isOpen()) {
            log.debug("Dialog take bars is open, taking bars");
            if (!pressSpace()) {
                return takeGoldBarsFromBarDispenser(log);
            }
            delay(200, 300);
            return takeGoldBarsFromBarDispenser(log);
        }

        if (!isTakeGoldBarsFromDispenserDialogOpen() && !clickOnDispenser(log)) {
            return takeGoldBarsFromBarDispenser(log);
        }

        log.debug("Clicked on bar dispenser");
        delay(500, 600);

        return takeGoldBarsFromBarDispenser(log);
    }


    public static boolean clickOnDispenser(BotLogger log) {
        delay(200);

        LocatableEntityQueryResults<GameObject> bar_dispenser = newQuery().names("Bar dispenser").results();
        log.info(bar_dispenser.size());
        for (GameObject gameObject : bar_dispenser)
            if (gameObject.click())
                return true;

        return clickOnDispenser(log);
    }

    public static boolean goNearBarDispenser() {
        if (!isPlayerAtConveyorTile()) {
            return true;
        }

        if (getLocal().isMoving()) {
            return true;
        }

        delay(500, 600);

        return BAR_DISPENSER_AREA.getRandomCoordinate().click() || goNearBarDispenser();
    }

    public static boolean isPlayerAtConveyorTile() {
        return CONVEYOR_TILE.getArea().contains(getLocal());
    }

    private static boolean isTakeGoldBarsFromDispenserDialogOpen() {
        return Interfaces.newQuery().actions("Take").results().size() > 0;
    }

    private static boolean pressSpace() {
        int spaceKey = 32;
        pressKey(spaceKey);
        delay(24, 39);
        return releaseKey(spaceKey);
    }

}