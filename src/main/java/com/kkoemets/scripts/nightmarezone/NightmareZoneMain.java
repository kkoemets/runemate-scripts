package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.presets.AbstractNightmareZoneScript;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.EngineListener;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.SkillListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;
import javafx.application.Platform;

import javax.annotation.Nullable;
import java.util.List;

import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.nightmarezone.scripts.NightmareZoneScriptFactory.getAll;

public class NightmareZoneMain extends LoopingBot implements MoneyPouchListener, SkillListener,
        InventoryListener, EngineListener {

    private String aSetting;
    private BotLogger log;
    private List<AbstractNightmareZoneScript> allScripts;
    @Nullable
    private AbstractNightmareZoneScript currentScript = null;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public NightmareZoneMain() {
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

        allScripts = getAll(log);
        Platform.runLater(() -> new NightmareZoneGui(this));
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onExperienceGained(SkillEvent event) {

    }

    @Override
    public void onItemAdded(ItemEvent event) {
        if (currentScript == null) {
            return;
        }
    }

    @Override
    public void onItemRemoved(ItemEvent event) {
        if (currentScript == null) {
            return;
        }
    }

    @Override
    public void onTickStart() {
        if (currentScript == null) {
            return;
        }
        try {
            currentScript.validate();
        } catch (Exception e) {
            log.severe(e.getMessage());
            currentScript = null;
            pause();
        }
    }

    @Override
    public void onLoop() {
        if (currentScript == null) {
            pause();
        } else {
            log.debug("Exec");
            currentScript.execute();
        }

        log.info("The end");
    }


    public List<AbstractNightmareZoneScript> getAllScripts() {
        return allScripts;
    }

    public void setCurrentScript(AbstractNightmareZoneScript currentScript) {
        this.currentScript = currentScript;
    }

}