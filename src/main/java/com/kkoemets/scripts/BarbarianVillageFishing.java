package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.kkoemets.api.common.player.PlayerHandler;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.api.common.inventory.ShiftDropper.dropAll;
import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.BarbarianVillageFishing.Action.*;
import static com.runemate.game.api.hybrid.local.Camera.setZoom;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Npcs.newQuery;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static java.lang.String.format;

public class BarbarianVillageFishing extends LoopingBot implements MoneyPouchListener {

    private final String ROD_FISHING_SPOT = "Rod Fishing spot";
    private String aSetting;
    private BotLogger log;
    private InteractionHandler interactionHandler;
    private PlayerHandler playerHandler;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public BarbarianVillageFishing() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((int) (ACTIVENESS_FACTOR_WHILE_WAITING.getAsDouble() * 10000));
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();

        interactionHandler = new InteractionHandler(log);
        playerHandler = new PlayerHandler(log);
        setZoom(0.027, 0.004);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        Action action = getAction();

        Boolean actionResult = handleAction(action);

        log.debug(format("Action was %s, result was %s", action, actionResult));
    }

    private Boolean handleAction(Action action) {
        if (action == DROP_ALL_FISH) {
            return dropFish();
        }

        if (action == IDLE) {
            return delay(234, 545);
        }

        if (action == CATCH_FISH) {
            return catchFish();
        }

        return null;
    }

    private Action getAction() {
        if (Inventory.isFull()) {
            return DROP_ALL_FISH;
        }

        if (!playerIsIdle()) {
            return IDLE;
        }

        if (playerIsIdle() && !newQuery().names(ROD_FISHING_SPOT).results().isEmpty()) {
            return CATCH_FISH;
        }

        return null;
    }


    private boolean catchFish() {
        return interactionHandler
                .turnCameraIfNecessaryAndInteract(newQuery()
                        .names(ROD_FISHING_SPOT).results().nearest(), getLocal(), "Lure") && delay(4500);
    }


    private boolean playerIsIdle() {
        return playerHandler.isPlayerIdle(getLocal());
    }

    private boolean dropFish() {
        if (Random.nextDouble(0, 1) < 0.15) {
            delay(24657, 34813);
        }

        log.debug("Inventory is full");
        while (!getTroutsAndSalmons().isEmpty()) {
            dropAll(getTroutsAndSalmons());
        }

        return true;
    }

    private SpriteItemQueryResults getTroutsAndSalmons() {
        return getItems("Raw salmon", "Raw trout");
    }

    enum Action {
        DROP_ALL_FISH,
        CATCH_FISH,
        IDLE
    }

}