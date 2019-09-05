package com.kkoemets.scripts;

import com.kkoemets.api.common.camera.MouseWheel;
import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.input.Mouse.*;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;

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
        log.info(Camera.getZoom()); // 0
        log.info(Camera.getYaw());
        log.debug(getLocal().getPosition());
        if ((Camera.getYaw() < 273 || Camera.getYaw() > 279) && Camera.getPitch() < 0.96) {
            MouseWheel.mouseWheelTurnTo(new Coordinate(getLocal().getPosition().getX() + 16, 4960,
                            0),
                    getLocal());
            delay(4546, 7600);

        }
//
//        press(Mouse.Button.WHEEL);
//        move(new InteractablePoint((int) (Mouse.getPosition().getX() + deltaMouseMoveX), (int) (Mouse.getPosition().getY() + Random.nextInt(-10, 10))));
//        release(Mouse.Button.WHEEL);
//
        Camera.turnTo(0.95);

        log.info("Pitch-" + Camera.getPitch());

        if (Camera.getZoom() > Random.nextDouble(0.051, 0.082)) {
            log.debug("hi");
            Camera.setZoom(0.0275, 0.0225);
        }


    }
}