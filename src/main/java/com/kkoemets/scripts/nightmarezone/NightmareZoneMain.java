package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.presets.AbstractNightmareZoneScript;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.EngineListener;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.SkillListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;
import javafx.application.Platform;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.nightmarezone.scripts.NightmareZoneScriptFactory.getAll;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toMap;

public class NightmareZoneMain extends LoopingBot implements MoneyPouchListener, SkillListener, EngineListener {

    private String aSetting;
    private BotLogger log;
    private List<AbstractNightmareZoneScript> allScripts;
    @Nullable
    private AbstractNightmareZoneScript currentScript = null;
    private Runnable onStop = () -> {
    };
    private Runnable onPause = () -> {
    };
    private Runnable onResume = () -> {
    };

    @Nullable
    private Long startTime = null;
    private Map<Skill, Integer> skillsInitialXp = new HashMap<>();
    private BiConsumer<Map<Skill, Integer>, Long> onXpGained = (xpGainMap, startTime) -> {
    };


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
        if (event.getType() != SkillEvent.Type.EXPERIENCE_GAINED) {
            return;
        }

        Skill skill = event.getSkill();
        skillsInitialXp.put(skill, skillsInitialXp.getOrDefault(skill, skill.getExperience()));

        Map<Skill, Integer> xpGainMap = skillsInitialXp.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getKey().getExperience() - e.getValue()));

        onXpGained.accept(xpGainMap, startTime);
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

    @Override
    public void onStop() {
        Platform.runLater(() -> onStop.run());
    }

    @Override
    public void onPause() {
        skillsInitialXp.clear();
        startTime = null;
        Platform.runLater(() -> onPause.run());
    }

    @Override
    public void onResume() {
        startTime = currentTimeMillis();
        Platform.runLater(() -> onResume.run());
    }

    public List<AbstractNightmareZoneScript> getAllScripts() {
        return allScripts;
    }

    public void setCurrentScript(AbstractNightmareZoneScript currentScript) {
        this.currentScript = currentScript;
    }

    public void setOnStop(Runnable onStop) {
        this.onStop = onStop;
    }

    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnXpGained(BiConsumer<Map<Skill, Integer>, Long> onXpGained) {
        this.onXpGained = onXpGained;
    }

}