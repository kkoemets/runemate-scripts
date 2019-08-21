package com.kkoemets.scripts;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
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
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.*;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class WineMaker extends LoopingBot implements MoneyPouchListener {

    private final String JUG_OF_WATER = "Jug of water";
    private final String GRAPES = "Grapes";
    private String aSetting;
    private BotLogger log;


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
//        setZoom(0.027, 0.004);
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
        } else if (delay(845) &&!Bank.isOpen()) {
            if (isPlayerIdle()) {
                if (hasPlayerJugsAndGrapesInInventory()) {
                    makeWine();
                    delay(2000);
                } else {
                    clickOnBankBoothToOpenBank();
                    delay(nextLong(523, 1121));
                }
            }
        } else {
            depositInventoryIfNeeded();

            setupDefaultQuantityIfNeeded();

            withdrawJugsAndGrapes();

            Bank.close();
        }
        log.debug("End of main loop");
    }

    private boolean isPlayerIdle() {
        return Players.getLocal().getAnimationId() == -1;
    }

    private boolean hasPlayerJugsAndGrapesInInventory() {
        return getLastJugOfWaterInInventory().isPresent() && getFirstGrapesInInvetory().isPresent();
    }


    private void clickOnBankBoothToOpenBank() {
        if (getNearestBankBooth().isPresent()) {
            openBank();
        } else {
            throw new IllegalStateException("Could not find a bank booth!");
        }
    }

    private void depositInventoryIfNeeded() {
        if (Inventory.getItems().size() > 0) {
            depositInventory();
            delay(bankSpecificReactionTime());
        }
    }

    private void setupDefaultQuantityIfNeeded() {
        if (getExactDefaultQuantity() != 14) {
            log.info("Default X quantity is not 14, setting to 14");
            setDefaultQuantity(14);
            delay(bankSpecificReactionTime());
        }

        if (getDefaultQuantity() != DefaultQuantity.X) {
            log.info("Default quantity is not X, setting to X");
            setDefaultQuantity(DefaultQuantity.X);
            delay(bankSpecificReactionTime());
        }
    }

    private Long bankSpecificReactionTime() {
        return REACTION_TIME.getAsLong() * 2 + nextLong(-17, 167);
    }

    private void withdrawJugsAndGrapes() {
        if (getJugOfWaterInBank().isPresent()) {
            log.info("Withdrawing jugs of water");
            getJugOfWaterInBank().get().click();
            delay(bankSpecificReactionTime());
        } else {
            throw new IllegalStateException("Could not find jugs of water!");
        }

        if (getGrapesInBank().isPresent()) {
            log.info("Withdrawing grapes");
            getGrapesInBank().get().click();
            delay(bankSpecificReactionTime());
        } else {
            throw new IllegalStateException("Could not find grapes!");
        }
    }

    private void openBank() {
        log.info("Opening bank");
        getNearestBankBooth().get().click();
    }

    private void makeWine() {
        log.info("Starting to make wine");
        getLastJugOfWaterInInventory().get().click();
        delay(REACTION_TIME.getAsLong() * 2 + nextLong(-10, 67));
        getFirstGrapesInInvetory().get().click();
        delay(REACTION_TIME.getAsLong() * 2 + nextLong(0, 67));
        delay(Random.nextLong(123, 235));
        Keyboard.pressKey(32); // space
    }

    private Optional<SpriteItem> getLastJugOfWaterInInventory() {
        SpriteItemQueryResults jugOfWater = getItems(JUG_OF_WATER);
        return jugOfWater.isEmpty() ? empty() : of(jugOfWater.get(jugOfWater.size() - 1));
    }

    private Optional<SpriteItem> getFirstGrapesInInvetory() {
        SpriteItemQueryResults grapes = getItems(GRAPES);
        return grapes.isEmpty() ? empty() : of(grapes.get(0));
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