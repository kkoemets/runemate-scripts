package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.Key.REACTION_TIME;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.DefaultQuantity.X;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class WineMaker extends LoopingBot implements MoneyPouchListener {

    private static int halfOfInventory = 14;
    private final String JUG_OF_WATER = "Jug of water";
    private final String GRAPES = "Grapes";
    private String aSetting;
    private BotLogger log;
    private InteractionHandler interactionHandler;


    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public WineMaker() {
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
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        if (!Optional.ofNullable(Players.getLocal()).isPresent()) {
            log.info("Player is not found, waiting...");
            return;
        }

        if (Players.getLocal().getAnimationId() != -1) {
            log.info("Player is making wine");
        } else if (delay(845) && !Bank.isOpen()) {
            if (isPlayerIdle()) {
                if (getItems(GRAPES).size() == halfOfInventory
                        && getItems(JUG_OF_WATER).size() == halfOfInventory) {
                    makeWine();
                } else {
                    clickOnBankBoothToOpenBank();
                    delay(nextLong(523, 1121));
                }
            }
        } else {
            depositInventoryAndGetWineMaterials();
        }
        log.debug("End of main loop");
    }

    private void depositInventoryAndGetWineMaterials() {
        depositInventoryIfNeeded();

        setupDefaultQuantityIfNeeded();

        withdrawJugsAndGrapes();

        if (getItems("Grapes", "Jug of water").size() == 28) {
            Bank.close();
        }
    }

    private boolean isPlayerIdle() {
        return Players.getLocal().getAnimationId() == -1;
    }

    private void clickOnBankBoothToOpenBank() {
        if (getNearestBankBooth().isPresent()) {
            openBank();
        } else {
            throw new IllegalStateException("Could not find a bank booth!");
        }
    }

    private void depositInventoryIfNeeded() {
        int amountOfJugOfWater = getItems(JUG_OF_WATER).size();
        int amountOfGrapes = getItems(GRAPES).size();
        int amountOfItemsInInventory = getItems().size();
        if (amountOfItemsInInventory < 1) {
            log.info("Inventory is empty, nothing to deposit");
            return;
        }
        if (amountOfJugOfWater == halfOfInventory
                && amountOfGrapes == halfOfInventory) {
            log.info("Inventory is full of grapes and jugs, will not deposit");
            return;
        }
        if (amountOfItemsInInventory == halfOfInventory
                && (amountOfGrapes == halfOfInventory ||
                amountOfJugOfWater == halfOfInventory)) {
            log.info("Inventory has 14 grapes or jugs, will not deposit");
            return;
        }

        log.info("Depositing inventory to bank");
        depositInventory();
        delay(bankSpecificReactionTime());
    }

    private void setupDefaultQuantityIfNeeded() {
        if (getExactDefaultQuantity() != halfOfInventory) {
            log.info("Default X quantity is not 14, setting to 14");
            setDefaultQuantity(halfOfInventory);
            delay(bankSpecificReactionTime());
        }

        delay(bankSpecificReactionTime());

        if (getDefaultQuantity() != X) {
            log.info("Default quantity is not X, setting to X");
            setDefaultQuantity(X);
            delay(bankSpecificReactionTime());
        }
    }

    private Long bankSpecificReactionTime() {
        return REACTION_TIME.getAsLong() * 2 + nextLong(-17, 167);
    }

    private void withdrawJugsAndGrapes() {
        if (!getJugOfWaterInBank().isPresent()) {
            throw new IllegalStateException("Could not find jugs of water!");
        }
        if (!getGrapesInBank().isPresent()) {
            throw new IllegalStateException("Could not find jugs of water!");
        }
        if (getItems(JUG_OF_WATER).size() != halfOfInventory) {
            log.info("Withdrawing jugs of water");
            getJugOfWaterInBank().get().click();
            delay(bankSpecificReactionTime());
        }

        if (getItems(GRAPES).size() != halfOfInventory) {
            log.info("Withdrawing grapes");
            getGrapesInBank().get().click();
            delay(bankSpecificReactionTime());
        }
    }

    private void openBank() {
        log.info("Opening bank");
        if (!getNearestBankBooth().get().isVisible()) {
            interactionHandler.turnCameraToCoordinate(getNearestBankBooth().get().getPosition(),
                    Players.getLocal());
        }
        getNearestBankBooth().get().click();
    }

    private void makeWine() {
        log.info("Starting to make wine");
        int spaceKey = 32;
        if (getWineMakingInterface().isEmpty()) {
            if (getItems().size() != 28) {
                log.info("Inventory is not full, not making wine");
                return;
            }
            if (getItems(GRAPES).size() != halfOfInventory) {
                log.info("Inventory does not have 14 grapes, not making wine");
                return;
            }
            if (getItems(JUG_OF_WATER).size() != halfOfInventory) {
                log.info("Inventory does not have 14 jugs, not making wine");
                return;
            }

            getItems().get(13).click();
            delay(REACTION_TIME.getAsLong() * 4 + nextLong(-121, 231));
            getItems().get(14).click();

            if (!getWineMakingInterface().isEmpty()) {
                log.info("Pressing space to make wine");
                Keyboard.pressKey(spaceKey);
            }

        }

        delay(Random.nextLong(123, 235));

        if (!getWineMakingInterface().isEmpty()) {
            log.info("Pressing space to make wine");
            Keyboard.pressKey(spaceKey);
        }
    }

    private InterfaceComponentQueryResults getWineMakingInterface() {
        return Interfaces.newQuery().names("Unfermented wine").results();
    }


    private Optional<SpriteItem> getJugOfWaterInBank() {
        SpriteItemQueryResults jugOfWater = Bank.getItems(JUG_OF_WATER);
        return jugOfWater.isEmpty() ? empty() : of(jugOfWater.get(0));
    }

    private Optional<SpriteItem> getGrapesInBank() {
        SpriteItemQueryResults grapes = Bank.getItems(GRAPES);
        return grapes.isEmpty() ? empty() : of(grapes.get(0));
    }

    private Optional<GameObject> getNearestBankBooth() {
        LocatableEntityQueryResults<GameObject> bankers = Banks.getLoadedBankBooths().sortByDistance();
        return bankers.isEmpty() ? empty() : of(bankers.get(0));
    }

}