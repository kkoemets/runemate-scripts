package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.Key.REACTION_TIME;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.DefaultQuantity.X;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Banks.getLoadedBankBooths;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Optional.*;

public class WineMaker extends LoopingBot implements MoneyPouchListener {

    private static final int halfOfInventory = 14;
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
        setLoopDelay((Random.nextInt(869, 1342)));
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
        if (!ofNullable(getLocal()).isPresent()) {
            log.info("Player is not found, waiting...");
            return;
        }

        if (delay(664, 987) && !isPlayerIdle()) {
            log.info("Player is making wine");
            return;
        }

        if (!Bank.isOpen() && !hasPlayer14JugsOfWaterAnd14Grapes()) {
            clickOnBankBoothToOpenBank();
        }

        if (Bank.isOpen()) {
            depositInventoryAndGetWineMaterials();
        }

        if (hasPlayer14JugsOfWaterAnd14Grapes() && !Bank.isOpen()) {
            makeWine();
        }

        log.debug("End of main loop");
    }

    private boolean hasPlayer14JugsOfWaterAnd14Grapes() {
        return getItems(GRAPES).size() == halfOfInventory
                && getItems(JUG_OF_WATER).size() == halfOfInventory;
    }

    private void depositInventoryAndGetWineMaterials() {
        depositInventoryIfNeeded();

        setupDefaultQuantityIfNeeded();

        withdrawJugsAndGrapes();

        if (getItems(GRAPES, JUG_OF_WATER).size() == 28) {
            Bank.close();
        }
    }

    private boolean isPlayerIdle() {
        return getLocal().getAnimationId() == -1;
    }

    private void clickOnBankBoothToOpenBank() {
        if (!getNearestBankBooth().isPresent()) {
            throw new IllegalStateException("Could not find a bank booth!");
        }
        openBank();
        delay(658, 878);
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
        SpriteItemQueryResults jugsAndGrapes = Bank.getItems(JUG_OF_WATER, GRAPES);

        if (jugsAndGrapes.get(0).getQuantity() < halfOfInventory) {
            throw new IllegalStateException("Not enough jugs of water in bank!");
        }
        if (jugsAndGrapes.get(1).getQuantity() < halfOfInventory) {
            throw new IllegalStateException("Not enough grapes in bank!");
        }

        jugsAndGrapes.sortByIndex();

        log.info("Withdrawing jugs and grapes");
        if (getItems(jugsAndGrapes.get(0).getId()).isEmpty()) {
            log.debug("Withdrawing first ingredient");
            jugsAndGrapes.get(0).click();
        }
        delay(bankSpecificReactionTime());
        if (getItems(jugsAndGrapes.get(1).getId()).isEmpty()) {
            log.debug("Withdrawing second ingredient");
            jugsAndGrapes.get(1).click();
        }
    }

    private void openBank() {
        log.info("Opening bank");
        if (!getNearestBankBooth().get().isVisible()) {
            log.debug("Bank booth is not visible, rotating camera");
            interactionHandler.turnCameraToCoordinate(getNearestBankBooth().get().getPosition(),
                    getLocal());
        }
        log.debug("Clicking on bank booth");
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

            if (!Inventory.isItemSelected()) {
                log.debug("Clicking on 14th item in inventory");
                getItems().get(13).click();
            }

            delay(567, 789);

            if (Inventory.isItemSelected()) {
                log.debug("Clicking on 15th item in inventory");
                getItems().get(14).click();
            }

            delay(567, 789);

            if (!getWineMakingInterface().isEmpty()) {
                log.info("Pressing space to make wine");
                pressKey(spaceKey);
            }

        }

        if (!getWineMakingInterface().isEmpty()) {
            log.info("Pressing space to make wine");
            pressKey(spaceKey);
        }
    }

    private InterfaceComponentQueryResults getWineMakingInterface() {
        return Interfaces.newQuery().names("Unfermented wine").results();
    }

    private Optional<GameObject> getNearestBankBooth() {
        LocatableEntityQueryResults<GameObject> bankers = getLoadedBankBooths()
                .sortByDistance();
        return bankers.isEmpty() ? empty() : of(bankers.get(0));
    }

}