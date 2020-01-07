package com.kkoemets.scripts.blastfurnace.coffer;

import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static java.lang.String.format;

public class CofferHandling {

    private CofferHandling() {
    }

    public static boolean isCofferEmpty(BotLogger log) {
        int cofferVarBit = 5357;
        int amountOfGold = load(cofferVarBit).getValue();
        if (amountOfGold == 0) {
            log.debug("Coffer is empty!");
            return true;
        }
        log.debug(format("Coffer has enough gold-%s gp", amountOfGold));
        return false;
    }

}