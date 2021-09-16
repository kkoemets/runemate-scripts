package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import com.kkoemets.scripts.nightmarezone.state.NightmareZoneConfigurationState;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;
import javafx.application.Platform;

import java.util.List;

import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.nightmarezone.NightmareZoneScriptFactory.getAll;

public class NightmareZoneMain extends LoopingBot implements MoneyPouchListener {

    private String aSetting;
    private BotLogger log;
    private List<AbstractNightmareZoneScript> allScripts;
    private NightmareZoneConfigurationState nightmareZoneConfigurationState;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public NightmareZoneMain() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);

        Platform.runLater(() -> new NightmareZoneGui(this));

        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((int) (ACTIVENESS_FACTOR_WHILE_WAITING.getAsDouble() * 10000));
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();
        nightmareZoneConfigurationState = new NightmareZoneConfigurationState();
        allScripts = getAll(log);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        AbstractNightmareZoneScript script = nightmareZoneConfigurationState.getCurrentScript();
        if (script == null) {
            log.info("No script selected");
            return;
        }
        script.execute();
        log.info("The end");
    }


    public List<AbstractNightmareZoneScript> getAllScripts() {
        return allScripts;
    }


    public NightmareZoneConfigurationState getNightmareZoneConfigurationState() {
        return nightmareZoneConfigurationState;
    }

}
