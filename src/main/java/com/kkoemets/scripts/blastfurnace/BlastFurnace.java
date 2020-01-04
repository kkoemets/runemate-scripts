package com.kkoemets.scripts.blastfurnace;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.kkoemets.scripts.blastfurnace.util.varbit.VarbitLogger;
import com.runemate.game.api.hybrid.local.Varbits;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.HashSet;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.blastfurnace.banking.BlastFurnaceBanking.goToBank;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class BlastFurnace extends LoopingBot implements MoneyPouchListener {

    private BlastFurnaceStateDecider stateDecider;
    private BlastFurnaceActionHandler actionHandler;
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
        stateDecider = new BlastFurnaceStateDecider(log);
        actionHandler = new BlastFurnaceActionHandler(log);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
//        BlastFurnaceAction action = stateDecider.decideNextAction();
//        log.info("Decided next action-" + action);
//        actionHandler.doAction(action);

//        if (notAtBlastFurnaceArea()) {
//            throw new IllegalStateException("Not at Blast Furnace area");
//        }


//        log.info("Bars are ready-" + (Varbits.load(955).getValue() == 24));
//        log.info("Foreman allows to use blast furnace-" + (Varbits.load(8354).getValue() == 1)); // incorrect
//        if (!script()) {
////            throw new IllegalStateException("Script return false");
//        }

        VarbitLogger.logChangedVarbits(getLogger(), new HashSet<>(asList(5357)));
        log.info("loop end");


    }

    public boolean script() {
//        if (cameraIsNotSet()) {
//            return setCamera();
//        }

        if (isCofferEmpty()) {
            log.info("Coffer is empty, going to bank");
            return goToBank(log);
        }

        goToBank(log);

        return true;
    }

    private boolean isCofferEmpty() {
        int cofferVarBit = 5357;
        int amountOfGold = Varbits.load(cofferVarBit).getValue();
        boolean isEmpty = amountOfGold < 0;
        if (isEmpty) {
            log.debug("Coffer is empty!");
            return true;
        }
        log.debug(format("Coffer has enough gold-%s gp", amountOfGold));
        return false;
    }


}