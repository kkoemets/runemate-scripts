package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.AfkContainer;
import com.kkoemets.api.common.interaction.InteractionHandler;
import com.kkoemets.api.common.inventory.InventoryHandler;
import com.kkoemets.api.common.npc.NpcHandler;
import com.kkoemets.api.common.player.PlayerHandler;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.Camera.setZoom;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.isFull;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Optional.ofNullable;

public class BarbarianVillageFishing extends LoopingBot implements MoneyPouchListener {

    private final String ROD_FISHING_SPOT = "Rod Fishing spot";
    private String aSetting;
    private BotLogger log;
    private InventoryHandler inventoryHandler;
    private InteractionHandler interactionHandler;
    private PlayerHandler playerHandler;
    private NpcHandler npcHandler;
    private AfkContainer afkContainer;

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

        inventoryHandler = new InventoryHandler(log);
        interactionHandler = new InteractionHandler(log);
        playerHandler = new PlayerHandler(log);
        npcHandler = new NpcHandler(log);
        afkContainer = new AfkContainer(log);
        setZoom(0.027, 0.004);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        Optional<Npc> rodFishingSpot =
                ofNullable(npcHandler.getNpcsSortedByDistance(ROD_FISHING_SPOT).get(0));
        Optional<Player> player = ofNullable(getLocal());

        if (isInventoryFull(player)) {
            if (Random.nextDouble(0, 1) < 0.15) {
                delay(afkContainer.getMediumAfkTime());
            }
            dropFish();
        } else if (isPlayerAbleToLureFish(rodFishingSpot, player)) {
            interactionHandler.turnCameraIfNecessaryAndInterract(rodFishingSpot.get(), player.get(),
                    "Lure");
            delay(4500);
        }

        log.debug(Camera.getZoom());
    }

    private boolean isInventoryFull(Optional<Player> player) {
        return isFull() && player.isPresent();
    }

    private boolean isPlayerAbleToLureFish(Optional<Npc> rodFishingSpot, Optional<Player> player) {
        return player.isPresent() && playerHandler.isPlayerIdle(player.get())
                && rodFishingSpot.isPresent();
    }

    private void dropFish() {
        log.debug("Inventory is full");
        inventoryHandler.shiftDropAllItems(getTroutsAndSalmons());
        SpriteItemQueryResults fishes;
        while (!(fishes = getTroutsAndSalmons().shuffle()).isEmpty()) {
            inventoryHandler.shiftDropAllItems(fishes);
        }
    }

    private SpriteItemQueryResults getTroutsAndSalmons() {
        return getItems("Raw salmon", "Raw trout");
    }

}