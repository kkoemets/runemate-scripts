package com.kkoemets.scripts.blastfurnace.dispenser;

import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.logger.BotLogger;

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

        if (!Equipment.contains("Ice gloves") && !Inventory.getItems("Ice gloves").get(0).click()) {
            return takeGoldBarsFromBarDispenser(log);
        }

        if (getLocal().isMoving()) {
            log.debug("Player is moving...");
            delay(2300, 2600);
            return takeGoldBarsFromBarDispenser(log);
        }

        if (isTakeGoldBarsFromDispenserDialogOpen() || isOpen()) {
            log.debug("Dialog take bars is open, taking bars");
            if (!pressSpace()) {
                return takeGoldBarsFromBarDispenser(log);
            }
            delay(2300, 2600);
            return takeGoldBarsFromBarDispenser(log);
        }

        if (!isTakeGoldBarsFromDispenserDialogOpen() && !clickOnDispenser()) {
            return takeGoldBarsFromBarDispenser(log);
        }

        log.debug("Clicked on bar dispenser");
        delay(2300, 2600);

        return takeGoldBarsFromBarDispenser(log);
    }


    private static boolean clickOnDispenser() {
        return GameObjects.newQuery().names("Bar dispenser").results().get(0).click();
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