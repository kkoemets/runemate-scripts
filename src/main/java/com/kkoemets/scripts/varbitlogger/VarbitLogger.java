package com.kkoemets.scripts.varbitlogger;

import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.HashSet;
import java.util.Map;

import static com.kkoemets.scripts.varbitlogger.VarbitHandler.filterChangedVarbits;
import static com.kkoemets.scripts.varbitlogger.VarbitHandler.getVarbits;
import static java.util.Arrays.asList;

public class VarbitLogger extends LoopingBot implements MoneyPouchListener {
    private String aSetting;
    private BotLogger log;

    public VarbitLogger() {
    }

    @Override
    public void onStart(String... args) {
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay(1);
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        Map<Integer, Integer> oldVarbits = null;
        HashSet<Integer> ignoredVarbits = new HashSet<>(asList(5357));
        while (true) {
            Map<Integer, Integer> newVarbits = getVarbits();

            Map<Integer, Integer> changedVarbits = oldVarbits != null ?
                    filterChangedVarbits(oldVarbits, newVarbits, ignoredVarbits) : newVarbits;
            if (!changedVarbits.isEmpty()) {
                log.info(String.format("Changed varbits-%s", changedVarbits));
            }

            oldVarbits = newVarbits;
        }

    }
}
