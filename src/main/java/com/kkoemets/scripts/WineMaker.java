package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.Timer;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.WineMaker.WineMakerScriptState.*;
import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.DefaultQuantity.X;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Banks.getLoadedBankBooths;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;
import static com.runemate.game.api.script.Execution.delay;
import static com.runemate.game.api.script.Execution.delayUntil;
import static java.math.BigDecimal.valueOf;
import static java.math.MathContext.DECIMAL128;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Optional.*;

public class WineMaker extends LoopingBot implements MoneyPouchListener {


    private static final int halfOfInventory = 14;
    private final String JUG_OF_WATER = "Jug of water";
    private final String GRAPES = "Grapes";
    private final double minInterval = 7.4;
    private final double maxInterval = 25.4;
    private final Timer timer = new Timer(minutesToMillis(minInterval), minutesToMillis(maxInterval));
    private String aSetting;
    private BotLogger log;
    private long stopTime = nextLong(minutesToMillis(minInterval), minutesToMillis(maxInterval));
    private InteractionHandler interactionHandler;
    private WineMakerScriptState state = WAIT_LOGIN;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public WineMaker() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((Random.nextInt(432, 576)));
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();
        interactionHandler = new InteractionHandler(log);
        timer.start();
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        if (timer.getElapsedTime() >= stopTime && state == WAIT) {
            delay(minutesToMillis(0.92), minutesToMillis(2.31));
            timer.reset();
            stopTime = nextLong(minutesToMillis(minInterval), minutesToMillis(maxInterval));
        }

        WineMakerScriptState returnedStateFromScript = script(state);
        if (returnedStateFromScript == state) {
            log.warn("Scripts next state-" + returnedStateFromScript + " is same as previously. "
                    + "Elapsed: " + valueOf((double) timer.getElapsedTime() / stopTime * 100)
                    .round(DECIMAL128).setScale(1, HALF_UP) + "%");
        }
        state = returnedStateFromScript;
    }

    private WineMakerScriptState script(WineMakerScriptState state) {
        log.debug("Current states is-" + state);
        switch (state) {
            case WAIT_LOGIN:
                return checkIfPlayerIsPresent();
            case OPEN_BANK:
                return openBank();
            case DECIDE_NEXT_STATE_FROM_OPENED_BANK:
                return getNextStateFromBanking();
            case BANK_ITEMS:
                return bankItems();
            case WITHDRAW_JUGS_AND_GRAPES:
                return withdrawJugsAndGrapes();
            case CLOSE_BANK:
                return closeBank();
            case MAKE_WINE:
                return makeWine();
            case PRESS_SPACE:
                return pressSpace();
            case WAIT:
                return delay(1234, 1432) && !isPlayerIdle() ? WAIT : OPEN_BANK;
            default:
                throw new IllegalStateException("Script crashed due to unhandled state-" + state);
        }
    }

    private WineMakerScriptState checkIfPlayerIsPresent() {
        if (!ofNullable(getLocal()).isPresent()) {
            log.info("Player is not found, waiting...");
            return WAIT_LOGIN;
        }
        return OPEN_BANK;
    }

    private WineMakerScriptState openBank() {
        if (!Bank.isOpen()) {
            clickOnBankBooth();
            return DECIDE_NEXT_STATE_FROM_OPENED_BANK;
        }

        WineMakerScriptState setupResultState = setupDefaultQuantityIfNeeded();
        if (setupResultState == OPEN_BANK) {
            return OPEN_BANK;
        }

        return DECIDE_NEXT_STATE_FROM_OPENED_BANK;
    }

    private WineMakerScriptState getNextStateFromBanking() {
        if (!Bank.isOpen()) {
            return OPEN_BANK;
        }

        if (!Inventory.getItems("Unfermented wine").isEmpty()) {
            return BANK_ITEMS;
        }

        if (isInventoryIsFullOfJugsAndGrapes()) {
            return CLOSE_BANK;
        }

        if (Inventory.getItems().size() == 28) {
            return BANK_ITEMS;
        }

        if (!Inventory.isEmpty()) {
            return BANK_ITEMS;
        }

        return WITHDRAW_JUGS_AND_GRAPES;
    }

    private WineMakerScriptState bankItems() {
        if (!Bank.isOpen()) {
            return OPEN_BANK;
        }

        if (isInventoryIsFullOfJugsAndGrapes()) {
            log.info("Inventory is full of grapes and jugs, will not deposit");
            return CLOSE_BANK;
        }

        if (getItems().isEmpty()) {
            log.info("Inventory is empty, nothing to deposit");
            return WITHDRAW_JUGS_AND_GRAPES;
        }

        if (hasInventory14JugsOrGrapesOnly()) {
            log.info("Inventory has 14 grapes or jugs, will not deposit");
            return WITHDRAW_JUGS_AND_GRAPES;
        }

        log.info("Depositing inventory to bank");
        delayUntil(Bank::depositInventory, 1244, 3232);
        return WITHDRAW_JUGS_AND_GRAPES;
    }

    private WineMakerScriptState withdrawJugsAndGrapes() {
        if (!Bank.isOpen()) {
            return OPEN_BANK;
        }

        takeFromBankJugsAndGrapes();

        return DECIDE_NEXT_STATE_FROM_OPENED_BANK;
    }

    private WineMakerScriptState closeBank() {
        Bank.close();
        return MAKE_WINE;
    }

    private WineMakerScriptState makeWine() {
        log.info("Starting to make wine");

        if (Bank.isOpen()) {
            return DECIDE_NEXT_STATE_FROM_OPENED_BANK;
        }

        if (pressSpace() == WAIT) {
            return WAIT;
        }

        if (getWineMakingInterface().isEmpty()) {
            if (!isInventoryIsFullOfJugsAndGrapes()) {
                return OPEN_BANK;
            }
            return clickInInventoryToMakeWine();
        }

        return WAIT;
    }

    private WineMakerScriptState pressSpace() {
        int spaceKey = 32;
        if (delay(234, 345) && !getWineMakingInterface().isEmpty()) {
            log.info("Pressing space to make wine");
            pressKey(spaceKey);
            delay(24, 39);
            releaseKey(spaceKey);
            return WAIT;
        }
        return MAKE_WINE;
    }

    private WineMakerScriptState setupDefaultQuantityIfNeeded() {
        if (!Bank.isOpen()) {
            return OPEN_BANK;
        }
        if (getExactDefaultQuantity() != halfOfInventory) {
            log.info("Default X quantity is not 14, setting to 14");
            setDefaultQuantity(halfOfInventory);
            return OPEN_BANK;
        }

        delayUntil(() -> getExactDefaultQuantity() == halfOfInventory, 5000);

        if (getDefaultQuantity() != X) {
            log.info("Default quantity is not X, setting to X");
            setDefaultQuantity(X);
            return OPEN_BANK;
        }
        return BANK_ITEMS;
    }

    private WineMakerScriptState clickInInventoryToMakeWine() {

        if (!Inventory.isItemSelected()) {
            log.debug("Clicking on 14th item in inventory");
            getItems().get(13).click();
            return MAKE_WINE;
        }

        log.debug("Clicking on 15th item in inventory");
        getItems().get(14).click();
        return PRESS_SPACE;

    }

    private void clickOnBankBooth() {
        if (!getNearestBankBooth().isPresent()) {
            throw new IllegalStateException("Could not find a bank booth!");
        }
        log.info("Opening bank");
        if (!getNearestBankBooth().get().isVisible()) {
            log.debug("Bank booth is not visible, rotating camera");
            interactionHandler.turnCameraToCoordinate(getNearestBankBooth().get().getPosition(),
                    getLocal());
        }
        log.debug("Clicking on bank booth");
        getNearestBankBooth().get().click();
    }

    private boolean isPlayerIdle() {
        Player player = getLocal();
        return player == null || player.getAnimationId() == -1;
    }

    private Optional<GameObject> getNearestBankBooth() {
        LocatableEntityQueryResults<GameObject> bankers = getLoadedBankBooths()
                .sortByDistance();
        return bankers.isEmpty() ? empty() : of(bankers.get(0));
    }

    private boolean hasInventory14JugsOrGrapesOnly() {
        return getItems().size() == halfOfInventory
                && (getItems(GRAPES).size() == halfOfInventory ||
                getItems(JUG_OF_WATER).size() == halfOfInventory);
    }

    private InterfaceComponentQueryResults getWineMakingInterface() {
        return Interfaces.newQuery().names("Unfermented wine").results();
    }

    private boolean isInventoryIsFullOfJugsAndGrapes() {
        return getItems(JUG_OF_WATER).size() == halfOfInventory
                && getItems(GRAPES).size() == halfOfInventory;
    }

    private void takeFromBankJugsAndGrapes() {
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

        delay(234, 342);

        if (getItems(jugsAndGrapes.get(1).getId()).isEmpty()) {
            log.debug("Withdrawing second ingredient");
            jugsAndGrapes.get(1).click();
        }
    }

    private long minutesToMillis(double minutes) {
        return (long) (minutes * 60 * 1000);
    }

    enum WineMakerScriptState {
        WAIT_LOGIN,
        OPEN_BANK,
        MAKE_WINE,
        WAIT,
        BANK_ITEMS,
        WITHDRAW_JUGS_AND_GRAPES,
        CLOSE_BANK,
        PRESS_SPACE,
        DECIDE_NEXT_STATE_FROM_OPENED_BANK,
    }

}