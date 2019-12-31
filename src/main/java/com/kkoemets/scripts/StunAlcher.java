package com.kkoemets.scripts;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;
import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.Skill.MAGIC;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.HIGH_LEVEL_ALCHEMY;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.STUN;
import static com.runemate.game.api.script.Execution.delay;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class StunAlcher extends LoopingBot implements MoneyPouchListener {
    private String aSetting;
    private BotLogger log;
    private long restTime;

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay(1274, 1542);
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();
        restTime = getRestTime();
    }

    private long getRestTime() {
        return currentTimeMillis() + nextLong(minutesToMillis(12.4), minutesToMillis(22.5));
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {

    }

    @Override
    public void onLoop() {
        Optional<Player> player = ofNullable(getLocal());
        if (!player.isPresent()) {
            log.info("Player is not visible yet, waiting...");
            return;
        }

        if (getNpcsWhoAttackPlayer().isEmpty()) {
            log.info("Please attack a target!");
            return;
        }

        if (currentTimeMillis() > restTime) {
            log.info("Rest time threshold, sleeping for a moment");
            delay(34565, 74553);
            restTime = getRestTime();
        }

        handleAction(Action.STUN);
        if (!STUN.isSelected()) {
            handleAction(Action.ALCH);
        }

        log.info("Loop end");
    }

    private Boolean handleAction(Action action) {
        if (action == Action.ALCH) {
            return alch(getMagicXp());
        }

        if (action == Action.STUN) {
            return stun(getMagicXp());
        }

        if (action == Action.IDLE) {
            return delay(76, 145);
        }

        return null;
    }

    private boolean stun(int magicXp) {
        if (magicXp != getMagicXp()) {
            return true;
        }

        if (!STUN.isSelected() && !STUN.activate()) {
            return stun(magicXp);
        }

       return getNpcsWhoAttackPlayer().get(0).click() && delay(642, 857) || stun(magicXp);
    }

    private boolean alch(int magicXp) {
        if (magicXp != getMagicXp()) {
            return true;
        }

        if (HIGH_LEVEL_ALCHEMY.isSelected() && !InterfaceWindows.getInventory().isOpen() &&
                HIGH_LEVEL_ALCHEMY.deactivate()) {
            return alch(magicXp);
        }

        if (!HIGH_LEVEL_ALCHEMY.isSelected() && !HIGH_LEVEL_ALCHEMY.activate()) {
            return alch(magicXp);
        }

        if (!InterfaceWindows.getInventory().isOpen()) {
            return alch(magicXp);
        }

        return Inventory.getItems("Yew longbow").first().click() && delay(452, 587) || alch(magicXp);
    }

    private List<Npc> getNpcsWhoAttackPlayer() {
        LocatableEntityQueryResults<Npc> npcs = Npcs.newQuery().reachable().results();
        return npcs.isEmpty() ? emptyList() : npcs.stream()
                .filter(npc -> npc.getTarget() != null && npc.getTarget().equals(getLocal())).collect(toList());
    }

    private int getMagicXp() {
        return MAGIC.getExperience();
    }

    private long minutesToMillis(double minutes) {
        return (long) (minutes * 60 * 1000);
    }

    enum Action {
        STUN,
        IDLE,
        ALCH
    }

}