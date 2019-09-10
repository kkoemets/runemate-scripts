package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.kkoemets.scripts.blastfurnace.BlastFurnaceActionHandler;
import com.kkoemets.scripts.blastfurnace.BlastFurnaceActionHandler.BlastFurnaceAction;
import com.kkoemets.scripts.blastfurnace.BlastFurnaceStateDecider;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;

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
        BlastFurnaceAction action = stateDecider.decideNextAction();
        log.info("Decided next action-" + action);
        actionHandler.doAction(action);
    }

}