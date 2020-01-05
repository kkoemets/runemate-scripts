package com.kkoemets.scripts.blastfurnace.coffer;

import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static java.lang.String.format;

public class CofferHandling {

    private CofferHandling() {
    }

    private static boolean isCofferEmpty(BotLogger log) {
        int cofferVarBit = 5357;
        int amountOfGold = load(cofferVarBit).getValue();
        boolean isEmpty = amountOfGold < 0;
        if (isEmpty) {
            log.debug("Coffer is empty!");
            return true;
        }
        log.debug(format("Coffer has enough gold-%s gp", amountOfGold));
        return false;
    }

}