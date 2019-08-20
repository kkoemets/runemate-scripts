package com.kkoemets.api.common.interaction;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.api.common.camera.MouseWheel.mouseWheelTurnTo;
import static com.kkoemets.api.common.interaction.execution.CMouse.accurateInteract;
import static com.kkoemets.playersense.CustomPlayerSense.Key.REACTION_TIME;
import static com.runemate.game.api.script.Execution.delay;

public class InteractionHandler {
    private BotLogger log;

    public InteractionHandler(BotLogger log) {
        this.log = log;
    }

    public void turnCameraIfNecessaryAndInterract(Npc npc, Player player, String action) {
        if (!npc.isVisible()) {
            mouseWheelTurnTo(npc.getPosition(), player);
        }
        delay(REACTION_TIME.getAsInteger());
        accurateInteract(npc, action);
    }
}