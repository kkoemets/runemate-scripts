package com.kkoemets.scripts.blastfurnace;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.blastfurnace.banking.BlastFurnaceBanking.bankGoldBarsAndWithdrawGoldOre;
import static com.kkoemets.scripts.blastfurnace.banking.BlastFurnaceBanking.openBank;
import static com.kkoemets.scripts.blastfurnace.conveyor.OreConveyorHandling.putGoldOreIntoConveyor;
import static com.kkoemets.scripts.blastfurnace.dispenser.BarDispenserHandling.hasBarDispenserGoldBars;
import static com.kkoemets.scripts.blastfurnace.dispenser.BarDispenserHandling.takeGoldBarsFromBarDispenser;

public class BlastFurnace extends LoopingBot implements MoneyPouchListener {

    private String aSetting;
    private BotLogger log;
    private InteractionHandler interactionHandler;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public BlastFurnace() {
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
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        script();
//        log.debug(Interfaces.newQuery().actions("Yes").results().size() > 0);
        log.info("Loop end");
    }

    public boolean script() {
//        i
//        f (cameraIsNotSet()) {
//            return setCamera();
//        }

//        if (isCofferEmpty()) {
//            log.info("Coffer is empty, going to bank");
//            return goToBank(log);
//        }
//
//        goToBank(log);
//
        if (!Bank.isOpen() && !Inventory.getItems("Gold bar").isEmpty()) {
            return openBank(log);
        }

        if (Bank.isOpen() && Inventory.getItems("Gold ore").isEmpty()) {
            log.info("Banking gold bars and withdrawing gold ore");
            return bankGoldBarsAndWithdrawGoldOre(log);
        }

//        if (Bank.isOpen() && Varbits.load(RUN_SLOWED_DEPLETION_ACTIVE.getId()).getValue() == 0) {
//            log.info("Stamina potion is not active, drinking");
//        }

        if (Bank.isOpen()) {
            return Bank.close();
        }

        if (!Inventory.getItems("Gold ore").isEmpty()) {
            return putGoldOreIntoConveyor(log);
        }

        if (hasBarDispenserGoldBars()) { //todo!!! if there is like 50 bars, how to handle?
            log.info("Dispenser has gold bars, taking them");
            return takeGoldBarsFromBarDispenser(log);
        }

        return true;
    }

}