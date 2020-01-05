package com.kkoemets.scripts.blastfurnace.dispenser;

import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.ICE_GLOVES;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog.isOpen;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BarDispenserHandling {

    private BarDispenserHandling() {
    }

    public static boolean hasBarDispenserGoldBars() {
        return load(NamedVarbit.BLAST_FURNACE_GOLD_BAR.getId()).getValue() > 0 ||
                load(NamedVarbit.BAR_DISPENSER.getId()).getValue() == 1 ||
                load(NamedVarbit.BAR_DISPENSER.getId()).getValue() == 2;
    }

    public static boolean takeGoldBarsFromBarDispenser(BotLogger log) {
        if (!hasBarDispenserGoldBars()) {
            return true;
        }

        if (!Equipment.contains(ICE_GLOVES) && !Inventory.getItems(ICE_GLOVES).get(0).click()) {
            return takeGoldBarsFromBarDispenser(log);
        }

        if (getLocal().isMoving()) {
            log.debug("Player is moving...");
            delay(200, 300);
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

        if (!isTakeGoldBarsFromDispenserDialogOpen() && !clickOnDispenser()) {
            return takeGoldBarsFromBarDispenser(log);
        }

        log.debug("Clicked on bar dispenser");
        delay(500, 600);

        return takeGoldBarsFromBarDispenser(log);
    }


    public static boolean clickOnDispenser() {
        delay(200);
        return GameObjects.newQuery().names("Bar dispenser").results().get(0).click() || clickOnDispenser();
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