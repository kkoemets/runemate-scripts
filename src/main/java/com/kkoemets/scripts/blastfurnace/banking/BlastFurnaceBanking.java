package com.kkoemets.scripts.blastfurnace.banking;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.isFull;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BlastFurnaceBanking {
    private static final String[] ITEMS_NEEDED_FOR_BLAST_FURNACE =
            {GOLD_ORE, ICE_GLOVES, GOLDSMITH_GAUNTLETS, "Coins"};

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
        delay(700, 900);

        return openBank(log);
    }

    public static boolean withdrawGoldOresFromOpenedBank(BotLogger log) {
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
            withdraw(GOLD_ORE, 99);
        }

        return withdrawGoldOresFromOpenedBank(log);
    }

    public static boolean depositGoldBarsToOpenedBank(BotLogger log) {
        if (Inventory.getItems(GOLD_BAR).isEmpty()) {
            log.debug("Successfully banked gold bars and took gold ore");
            return true;
        }

        if (isInventoryContainsGoldBars()) {
            depositAllExcept(ITEMS_NEEDED_FOR_BLAST_FURNACE);
        }

        return depositGoldBarsToOpenedBank(log);
    }

    public static boolean isInventoryContainsGoldBars() {
        return !Inventory.getItems(GOLD_BAR).isEmpty();
    }

    public static boolean closeBank() {
        return Bank.close() || closeBank();
    }

    public static boolean isGoldOresInInventory() {
        return !Inventory.getItems(GOLD_ORE).isEmpty();
    }

    public static boolean bankEverythingBesidesGloves() {
        if (Inventory.getItems().size() <= 2 && !Inventory.getItems(ICE_GLOVES, GOLDSMITH_GAUNTLETS).isEmpty()) {
            return true;
        }
        if (!depositAllExcept(ICE_GLOVES, GOLDSMITH_GAUNTLETS)) {
            return bankEverythingBesidesGloves();
        }

        delay(500);

        return bankEverythingBesidesGloves();
    }

}