package com.kkoemets.api.common.inventory;

import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;

import java.util.function.Function;

import static com.kkoemets.playersense.CustomPlayerSense.Key.SHIFT_DROP_REACTION_TIME;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.script.Execution.delay;

public class ShiftDropper {

    private ShiftDropper() {
    }

    public static void dropAll(SpriteItemQueryResults items) {
        holdShiftAndReleaseAfterMethodComplete(ShiftDropper::dropItems, items);
    }

    private static void holdShiftAndReleaseAfterMethodComplete(Function<SpriteItemQueryResults, Boolean> method,
                                                               SpriteItemQueryResults items) {
        pressKey(16);
        method.apply(items);
        releaseKey(16);
    }

    private static boolean dropItems(SpriteItemQueryResults items) {
        items.forEach(ShiftDropper::dropItem);
        return true;
    }

    private static void dropItem(SpriteItem item) {
        double randomPct = Random.nextDouble(0, 1);
        if (randomPct < 0.1) {
            return;
        }
        item.click();
        delay(SHIFT_DROP_REACTION_TIME.getAsInteger());
    }

}