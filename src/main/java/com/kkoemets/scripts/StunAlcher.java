package com.kkoemets.scripts;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;
import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.input.Mouse.Button.LEFT;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.HIGH_LEVEL_ALCHEMY;
import static com.runemate.game.api.osrs.local.hud.interfaces.Magic.STUN;
import static com.runemate.game.api.script.Execution.delay;
import static com.runemate.game.api.script.Execution.delayUntil;
import static java.lang.String.format;
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
        log.info(format("Script-{0}", "Stun alcher"));

        Optional<Player> player = ofNullable(getLocal());
        if (!player.isPresent()) {
            log.info("Player is not visible yet, waiting...");
            return;
        }
        log.info("Animation id-" + getLocal().getAnimationId());

        if (!getNpcsWhoAttackPlayer().isEmpty()) {
            log.info("Player is under attack!");
            actOnState(DO_STUN);

            if (!STUN.isSelected()) {
                actOnState(DO_ALCH);
            }
        }

    }

    private boolean isPlayerIdle() {
        return getLocal().getAnimationId() == -1 || getLocal().getAnimationId() == 1156;
    }

    private List<Npc> getNpcsWhoAttackPlayer() {
        LocatableEntityQueryResults<Npc> npcs = Npcs.newQuery().reachable().results();
        return npcs.isEmpty() ? emptyList() : npcs.stream().filter(npc -> npc.getTarget() != null &&
                npc.getTarget().equals(getLocal())).collect(toList());
    }

    private void actOnState(String state) {
        log.info("Input state-" + state);
        switch (state) {
            case IDLE:
                log.info("Player is idle");
                break;
            case DO_STUN:
                log.info("Trying to stun");

                if (!isPlayerIdle()) {
                    log.info("Player is not idle");
                    return;
                }

                List<Npc> npcsWhoAttackPlayer = getNpcsWhoAttackPlayer();
                if (npcsWhoAttackPlayer.isEmpty()) {
                    log.warn("Tried to cast a spell but was not interacting with anyone");
                    return;
                }

                while (!STUN.isSelected()) {
                    delayUntil(() -> STUN.isSelected() || (STUN.activate() && delay(92, 164)));
                }

                while (STUN.isSelected() && delay(345, 466)) {
                    log.info("Boom! Shooting stun!");
                    getNpcsWhoAttackPlayer().get(0).click();
                }

                break;
            case DO_ALCH:
                log.info("Trying to alch");
                delayUntil(this::isPlayerIdle);

                while (!HIGH_LEVEL_ALCHEMY.isSelected()) {
                    delay(123, 242);
                    HIGH_LEVEL_ALCHEMY.activate();
                }

                delayUntil(() -> delay(333, 373) &&
                        isPlayerIdle());

                while (HIGH_LEVEL_ALCHEMY.isSelected()) {
                    delay(245, 366);
                    log.info("Boom! Alching!");
                    Mouse.click(LEFT);
                }

                break;
            default:
                log.warn("Unknown state-" + state);
        }
    }
}