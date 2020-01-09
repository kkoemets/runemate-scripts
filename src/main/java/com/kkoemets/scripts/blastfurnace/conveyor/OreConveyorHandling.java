package com.kkoemets.scripts.blastfurnace.conveyor;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.GOLDSMITH_GAUNTLETS;
import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.GOLD_ORE;
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
        if (getItems(GOLD_ORE).isEmpty()) {
            return true;
        }

        if (getLocal().isMoving()) {
            log.debug("Walking to ore conveyor...");
            delay(1300, 1600);
            return putGoldOreIntoConveyor(log);
        }

        equipGoldSmithGauntlets();

        if (ChatDialog.isOpen() && ChatDialog.getText().contains("permission")) {
            return payForeman(log);
        }

        for (GameObject gameObject : newQuery().names("Conveyor belt").results())
            if (gameObject.interact("Put-ore-on"))
                break;

        delay(600, 800);

        return putGoldOreIntoConveyor(log);
    }

    public static boolean equipGoldSmithGauntlets() {
        if (contains(GOLDSMITH_GAUNTLETS)) {
            return true;
        }
        if (!getItems(GOLDSMITH_GAUNTLETS).get(0).click()) {
            return equipGoldSmithGauntlets();
        }

        delay(700, 900);
        return equipGoldSmithGauntlets();
    }
}
