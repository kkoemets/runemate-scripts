package com.kkoemets.api.interaction;

import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.Key.AFK_MEDIUM_TIME;
import static com.runemate.game.api.hybrid.util.calculations.Random.nextLong;

public class AfkContainer {
    private BotLogger log;

    public AfkContainer(BotLogger log) {
        this.log = log;
    }

    public  Long getMediumAfkTime() {
        return AFK_MEDIUM_TIME.getAsLong() + nextLong(-10000, 10000);
    }
}
