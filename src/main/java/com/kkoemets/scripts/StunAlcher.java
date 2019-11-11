package com.kkoemets.scripts;

import com.kkoemets.api.common.FailSafeCounter;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;
import java.util.Optional;

import static com.kkoemets.api.common.FailSafeCounter.createCounter;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.Skill.MAGIC;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.HIGH_LEVEL_ALCHEMY;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.STUN;
import static com.runemate.game.api.script.Execution.delay;
import static com.runemate.game.api.script.Execution.delayUntil;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class StunAlcher extends LoopingBot implements MoneyPouchListener {
    private final static String IDLE = "IDLE";
    private final static String DO_STUN = "DO_STUN";
    private final static String DO_ALCH = "DO_ALCH";
    private String aSetting;
    private BotLogger log;

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

        log.info("Player is under attack!");
        actOnState(DO_STUN);
        if (!STUN.isSelected()) {
            actOnState(DO_ALCH);
        }
    }

    private void actOnState(String state) {
        log.info("Input state-" + state);
        switch (state) {
            case IDLE:
                log.info("Player is idle");
                break;
            case DO_STUN:
                stun();
                break;
            case DO_ALCH:
                alch();
                break;
            default:
                log.warn("Unknown state-" + state);
        }
    }

    private void stun() {
        int xpBeforeStun = getMagicXp();
        FailSafeCounter counter = createCounter();
        do {
            log.info("Trying to stun");

            log.info(counter.increase());
            List<Npc> npcsWhoAttackPlayer = getNpcsWhoAttackPlayer();
            if (npcsWhoAttackPlayer.isEmpty()) {
                log.warn("Tried to cast a spell but was not interacting with anyone");
                return;
            }

            if (!STUN.isSelected()) {
                STUN.activate();
            }

            if (getNpcsWhoAttackPlayer().get(0).click()) return;
            log.info("Boom! Shooting stun!");

        } while (delay(223, 245) && (xpBeforeStun == getMagicXp() || STUN.isSelected()));

    }

    private void alch() {
        int xpBeforeAlch = getMagicXp();
        FailSafeCounter counter = createCounter();

        do {
            log.info("Trying to alch");
            log.info(counter.increase());

            if (!HIGH_LEVEL_ALCHEMY.isSelected()) {
                log.info("Selecting high alch");
                HIGH_LEVEL_ALCHEMY.activate();
            }

            delay(256, 367);

            if (HIGH_LEVEL_ALCHEMY.isSelected()) {
                delayUntil(() -> InterfaceWindows.getInventory().isOpen(), 456, 566);
                if (InterfaceWindows.getInventory().isOpen()) {
                    log.info("Clicking on inventory item to high alch");
                    Optional<SpriteItem> itemToAlch = ofNullable(Inventory.getItems("Yew longbow").first());
                    if (!itemToAlch.isPresent()) throw new IllegalArgumentException("Where are your bows?");
                    if (itemToAlch.get().click()) return;
                }
            }
        } while (delay(552, 678) && (xpBeforeAlch == getMagicXp() || HIGH_LEVEL_ALCHEMY.isSelected()));
    }

    private List<Npc> getNpcsWhoAttackPlayer() {
        LocatableEntityQueryResults<Npc> npcs = Npcs.newQuery().reachable().results();
        return npcs.isEmpty() ? emptyList() : npcs.stream().filter(npc -> npc.getTarget() != null &&
                npc.getTarget().equals(getLocal())).collect(toList());
    }

    private int getMagicXp() {
        return MAGIC.getExperience();
    }

}