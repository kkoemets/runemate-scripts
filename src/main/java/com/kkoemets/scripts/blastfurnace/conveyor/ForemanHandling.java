package com.kkoemets.scripts.blastfurnace.conveyor;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

class ForemanHandling {

    private ForemanHandling() {
    }

    public static boolean payForeman(BotLogger log) {
        if (getItems("Coins").isEmpty() ||
                getAmountOfCoinsInInventory() < 2500) {
            throw new IllegalStateException("Trying to pay foreman but 2500 gp");
        }

        return pay(log, getAmountOfCoinsInInventory());

    }

    private static boolean pay(BotLogger log, int startingCoinsAmount) {
        if (getAmountOfCoinsInInventory() != startingCoinsAmount) {
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Walking to foreman...");
            delay(2300, 2600);
            return pay(log, startingCoinsAmount);
        }

        if (ChatDialog.isOpen() && ChatDialog.getText() == null &&
                Interfaces.newQuery().actions("Yes").results().size() > 0) {
            log.debug("Dialog to pay foreman is open");

            pressKey(49);
            return pay(log, startingCoinsAmount);
        }

        if (!Npcs.newQuery().names("Blast Furnace Foreman").results().get(0).interact("Pay")) {
            return pay(log, startingCoinsAmount);
        }

        delay(2300, 2600);

        return pay(log, startingCoinsAmount);
    }

    private static int getAmountOfCoinsInInventory() {
        return getItems("Coins").get(0).getQuantity();
    }

}