package com.kkoemets.scripts.blastfurnace.banking;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.isFull;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BlastFurnaceBanking {
    private static final String[] ITEMS_NEEDED_FOR_BLAST_FURNACE =
            {"Gold ore", "Ice gloves", "Goldsmith gauntlets", "Coins"};

    private BlastFurnaceBanking() {
    }

    public static boolean openBank(BotLogger log) {
        if (isOpen()) {
            log.debug("Bank interface is open");
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Walking to bank...");
            delay(1300, 1600);
            return openBank(log);
        }

        if (!isOpen() && !GameObjects.newQuery().names("Bank chest").results().get(0).click()) {
            return openBank(log);
        }

        log.debug("Clicked on bank chest");
        delay(1300, 1600);

        return openBank(log);
    }

    public static boolean withdrawGoldOresFromOpenedBank(BotLogger log) {
        if (!isOpen()) {
            log.warn("Tried to take gold bars from bank but bank interface was not opened!");
            return false;
        }

        depositAllExcept(ITEMS_NEEDED_FOR_BLAST_FURNACE);

        if (isFull() && !isGoldOresInInventory()) {
            log.warn("Tried to take gold ores but inventory was full!");
            return depositGoldBarsToOpenedBank(log);
        }


        if (isGoldOresInInventory()) {
            log.debug("Successfully took gold ore");
            return true;
        }

        if (!isFull()) {
            withdraw("Gold ore", 99);
        }

        return withdrawGoldOresFromOpenedBank(log);
    }

    public static boolean depositGoldBarsToOpenedBank(BotLogger log) {
        if (!isOpen()) {
            log.warn("Tried to take gold bars from bank but bank interface was not opened!");
            return false;
        }

        if (Inventory.getItems("Gold bar").isEmpty()) {
            log.debug("Successfully banked gold bars and took gold ore");
            return true;
        }

        if (isInventoryContainsGoldBars()) {
            depositAllExcept(ITEMS_NEEDED_FOR_BLAST_FURNACE);
        }

        return depositGoldBarsToOpenedBank(log);
    }

    public static boolean isInventoryContainsGoldBars() {
        return !Inventory.getItems("Gold bar").isEmpty();
    }

    public static boolean isGoldOresInInventory() {
        return !Inventory.getItems("Gold ore").isEmpty();
    }

}