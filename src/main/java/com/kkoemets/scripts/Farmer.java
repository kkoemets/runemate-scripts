package com.kkoemets.scripts;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.osrs.local.hud.interfaces.Magic;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static java.util.Objects.isNull;

public class Farmer extends LoopingBot implements MoneyPouchListener {

    private final Coordinate FALADOR_TREE_SPOT = createCoordinate(3001, 3375, 0);
    private final Coordinate VARROCK_TREE_SPOT = createCoordinate(3228, 3457, 0);
    private final Coordinate TAVERLY_TREE_SPOT = createCoordinate(2934, 3438, 0);
    private final Coordinate LUMBRIDGE_TREE_SPOT = createCoordinate(3195, 3229, 0);
    private String aSetting;
    private BotLogger log;

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay(2344, 3255);
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();
    }


    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {

    }

    @Override
    public void onLoop() {
//        goToFaladorTreeSpot();
//        log.debug(getLocal().getPosition());
//        goToFaladorTreeSpot();
//        goTo(TAVERLY_TREE_SPOT);
//        goToVarrockTreeSpot();
//        goToLumbridgeFarmingSpot();

        if (Players.getLocal().getAnimationId() == -1 && !GameObjects.newQuery().names("Tree " +
                "patch").results().get(0).interact("Rake")) {
            Inventory.getItems("Yew sapling").get(0).click();
            GameObjects.newQuery().names("Tree patch").results().get(0).getPosition().click();
        }
        if (!GameObjects.newQuery().names("Yew sapling", "Yew tree").results().isEmpty()) {
            Npc farmingNpc = Npcs.newQuery().names("Heskel").results().get(0);
            log.debug(farmingNpc);

            if (!ChatDialog.isOpen()) {
                farmingNpc.interact("Pay");
            }

            Execution.delayUntil(ChatDialog::isOpen, 10000);
            ChatDialog.getOption(1).select();
            Execution.delayUntil((() -> ChatDialog.getContinue() != null), 10000);
            ChatDialog.getContinue().select();
        }


    }

    private void goToLumbridgeFarmingSpot() {
        Magic.LUMBRIDGE_TELEPORT.activate();
        while (!LUMBRIDGE_TREE_SPOT.isReachable()) goTo(LUMBRIDGE_TREE_SPOT);
    }

    private void goToVarrockTreeSpot() {
        Magic.VARROCK_TELEPORT.activate("Cast");
        while (true) goTo(VARROCK_TREE_SPOT);
    }

    private void goToFaladorTreeSpot() {
//        Magic.FALADOR_TELEPORT.activate();
        while (true) goTo(FALADOR_TREE_SPOT);

    }

    private void goTo(Coordinate coordinate) {
        WebPath webPath = Traversal.getDefaultWeb().getPathBuilder().buildTo(coordinate);
        if (!isNull(webPath)) {
            webPath.step();
            log.debug(getLocal().getPosition());
            return;
        }
        log.warn("Failed to go to " + coordinate);
    }

    private Coordinate createCoordinate(int x, int y, int z) {
        return new Coordinate(x, y, z);
    }

}
