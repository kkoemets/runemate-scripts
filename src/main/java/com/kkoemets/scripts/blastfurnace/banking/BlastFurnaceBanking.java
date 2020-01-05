package com.kkoemets.scripts.blastfurnace.banking;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.isFull;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BlastFurnaceBanking {

    private BlastFurnaceBanking() {
    }

    public static boolean openBank(BotLogger log) {
        if (isOpen()) {
            log.debug("Bank interface is open");
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Walking to bank...");
            delay(2300, 2600);
            return openBank(log);
        }

        if (!isOpen() && !GameObjects.newQuery().names("Bank chest").results().get(0).click()) {
            return openBank(log);
        }

        log.debug("Clicked on bank chest");
        delay(2300, 2600);

        return openBank(log);
    }

    public static boolean bankGoldBarsAndWithdrawGoldOre(BotLogger log) {
        if (!isOpen()) {
            return false;
        }

        if (!Inventory.getItems("Gold ore").isEmpty() && Inventory.getItems("Gold bar").isEmpty()) {
            log.debug("Successfully banked gold bars and took gold ore");
            return true;
        }

        if (!Inventory.getItems("Gold bar").isEmpty() &&
                !depositAllExcept("Gold ore", "Ice gloves", "Goldsmith gauntlets", "Coins")) {
            return bankGoldBarsAndWithdrawGoldOre(log);
        }

        if (!isFull()) {
            withdraw("Gold ore", 99);
        }

        return bankGoldBarsAndWithdrawGoldOre(log);
    }

}