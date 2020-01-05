package com.kkoemets.scripts.blastfurnace.conveyor;

import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.conveyor.ForemanHandling.payForeman;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Equipment.contains;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.GameObjects.newQuery;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class OreConveyorHandling {

    private OreConveyorHandling() {
    }

    public static boolean putGoldOreIntoConveyor(BotLogger log) {
        if (getItems("Gold ore").isEmpty()) {
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Walking to ore conveyor...");
            delay(2300, 2600);
            return putGoldOreIntoConveyor(log);
        }

        if (!contains("Goldsmith gauntlets") && !getItems("Goldsmith gauntlets").get(0).click()) {
            log.debug("Equipping goldsmith gauntlets");
            return putGoldOreIntoConveyor(log);
        }

        if (ChatDialog.isOpen() && ChatDialog.getText().contains("permission")) {
            return payForeman(log);
        }

        if (!newQuery().names("Conveyor belt").results().get(0).interact("Put-ore-on")) {
            return putGoldOreIntoConveyor(log);
        }

        delay(2300, 2600);

        return putGoldOreIntoConveyor(log);
    }
}
