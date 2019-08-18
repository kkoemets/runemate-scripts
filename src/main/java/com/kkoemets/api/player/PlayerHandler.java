package com.kkoemets.api.player;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.script.framework.logger.BotLogger;

public class PlayerHandler {
    private BotLogger log;

    public PlayerHandler(BotLogger log) {
        this.log = log;
    }

    public boolean isPlayerIdle(Player player) {
        return player.getAnimationId() == -1 && !player.isMoving();
    }
}