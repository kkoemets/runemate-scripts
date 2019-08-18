package com.kkoemets.api.inventory;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.function.Function;

import static com.kkoemets.playersense.CustomPlayerSense.Key.SHIFT_DROP_REACTION_TIME;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.script.Execution.delay;

public class InventoryHandler {
    private BotLogger log;

    public InventoryHandler(BotLogger log) {
        this.log = log;
    }

    public void shiftDropAllItems(SpriteItemQueryResults items) {
        holdShiftAndReleaseAfterMethodComplete((this::dropItems), items);
    }

    private void holdShiftAndReleaseAfterMethodComplete(Function<SpriteItemQueryResults, Boolean> method,
                                                        SpriteItemQueryResults items) {
        pressKey(16);
        method.apply(items);
        releaseKey(16);
    }

    private boolean dropItems(SpriteItemQueryResults items) {
        for (SpriteItem fish : items) {
            log.debug("Dropping " + fish);
            dropItem(fish);
        }
        return true;
    }

    private void dropItem(SpriteItem item) {
        double randomPct = Random.nextDouble(0, 1);
        if (randomPct < 0.1) {
            return;
        }
        clickOnItem(item);
        delay(SHIFT_DROP_REACTION_TIME.getAsInteger());
    }

    private void clickOnItem(SpriteItem item) {
        item.click();
    }

}