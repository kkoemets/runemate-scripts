package com.kkoemets.scripts.blastfurnace;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.*;
import static com.kkoemets.scripts.blastfurnace.banking.BlastFurnaceBanking.*;
import static com.kkoemets.scripts.blastfurnace.camera.BlastFurnaceCameraConfigurer.cameraIsNotSet;
import static com.kkoemets.scripts.blastfurnace.camera.BlastFurnaceCameraConfigurer.setCamera;
import static com.kkoemets.scripts.blastfurnace.coffer.CofferHandling.isCofferEmpty;
import static com.kkoemets.scripts.blastfurnace.conveyor.OreConveyorHandling.putGoldOreIntoConveyor;
import static com.kkoemets.scripts.blastfurnace.dispenser.BarDispenserHandling.*;
import static com.kkoemets.scripts.blastfurnace.stamina.StaminaDrinking.hasStaminaExpired;
import static com.kkoemets.scripts.blastfurnace.stamina.StaminaDrinking.takeStaminaFromOpenedBankAndCloseBankAndDrink;
import static com.kkoemets.scripts.varbitlogger.NamedVarbit.BAR_DISPENSER;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

public class BlastFurnace extends LoopingBot implements MoneyPouchListener {

    private String aSetting;
    private BotLogger log;
    private InteractionHandler interactionHandler;

    public BlastFurnace() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((Random.nextInt(124, 325)));
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
        script();
        log.info("Loop end");
    }

    public boolean script() {
        if (cameraIsNotSet()) {
            return setCamera();
        }

        if (!validateGameState()) {
            return true;
        }

        if (Bank.isOpen()) {
            return doBanking();

        }

        if (!Bank.isOpen()) {
            return doActions();
        }

        return true;
    }

    private boolean validateGameState() {
        if (getLocal() == null) {
            log.info("Waiting for player to appear");
            return false;
        }

        if (isCofferEmpty(log)) {
            throw new IllegalStateException("Coffer is empty!");
        }

        if (Inventory.getItems(ICE_GLOVES).isEmpty() && !Equipment.contains("Ice gloves")) {
            throw new IllegalStateException("No ice gloves in inv or equipped");
        }
        if (Inventory.getItems(GOLDSMITH_GAUNTLETS).isEmpty() && !Equipment.contains(GOLDSMITH_GAUNTLETS)) {
            throw new IllegalStateException("No goldsmith gauntlets in inv or equipped");
        }

        return true;
    }

    private boolean doBanking() {
        if (isInventoryContainsGoldBars()) {
            return depositGoldBarsToOpenedBank(log);
        }

        if (hasStaminaExpired()) {
            return takeStaminaFromOpenedBankAndCloseBankAndDrink() && openBank(log);
        }

        if (hasBarDispenserGoldBars()) {
            return bankEverythingBesidesGloves() && closeBank();
        }

        if (!isGoldOresInInventory()) {
            log.info("Banking gold bars and withdrawing gold ore");
            return withdrawGoldOresFromOpenedBank(log) && closeBank();
        }

        return true;
    }

    private Boolean doActions() {
        if (!Inventory.getItems(GOLD_ORE).isEmpty()) {
            return putGoldOreIntoConveyor(log);
        }

        if (hasBarDispenserGoldBars()) {
            if (Inventory.isFull()) {
                return openBank(log);
            }
            log.info("Dispenser has gold bars, taking them");
            return takeGoldBarsFromBarDispenser(log);
        }

        if (isInventoryContainsGoldBars()) {
            return openBank(log);
        }

        if (isPlayerAtConveyorTile() && Inventory.getItems(GOLD_ORE).isEmpty()) {
            return goNearBarDispenser();
        }

        if (isPlayerIdle() && delay(1500, 2000) && isPlayerIdle() && delay(1500, 2000) && isPlayerIdle()) {
            return openBank(log);
        }

        return true;
    }

    private boolean isPlayerIdle() {
        return !Bank.isOpen() && Inventory.getItems(GOLD_ORE, GOLD_BAR).isEmpty() && !hasBarDispenserGoldBars() &&
                load(BAR_DISPENSER.getId()).getValue() == 0;
    }

}