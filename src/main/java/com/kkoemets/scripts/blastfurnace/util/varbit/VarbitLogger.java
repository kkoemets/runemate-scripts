package com.kkoemets.scripts.blastfurnace.util.varbit;

import com.runemate.game.api.hybrid.local.Varbits;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Map;
import java.util.Set;

import static com.kkoemets.scripts.blastfurnace.util.varbit.VarbitHandler.getVarbits;

public class VarbitLogger {

    public static void logChangedVarbits(BotLogger log, Set<Integer> ignoredVarbits) {
        Map<Integer, Integer> oldVarbits = null;
        while (true) {
            Map<Integer, Integer> newVarbits = getVarbits();
            Map<Integer, Integer> changedVarbits = oldVarbits != null ?
                    VarbitHandler.filterChangedVarbits(oldVarbits, newVarbits, ignoredVarbits) : newVarbits;
            oldVarbits = newVarbits;

            if (!changedVarbits.isEmpty()) {
                log.info(String.format("Changed varbits-%s", changedVarbits));
            }
            log.info("Conveyor has ores-" + (Varbits.load(955).getValue() != 0));
            log.info(Varbits.load(955).getValue());
        }
    }
}
