package com.kkoemets.scripts.blastfurnace;

import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.input.Keyboard.pressKey;
import static com.runemate.game.api.hybrid.input.Keyboard.releaseKey;
import static com.runemate.game.api.hybrid.input.Mouse.*;
import static com.runemate.game.api.hybrid.input.Mouse.release;
import static com.runemate.game.api.hybrid.location.Area.polygonal;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static com.runemate.game.api.script.Execution.delayUntil;

public class BlastFurnaceActionHandler {
    private BotLogger log;

    public BlastFurnaceActionHandler(BotLogger log) {
        this.log = log;
    }

    private final String GOLDSMITH_GAUNTLETS = "Goldsmith gauntlets";
    private final String ICE_GLOVES = "Ice gloves";
    private final String GOLD_ORE = "Gold ore";
    private final String GOLD_BAR = "Gold bar";
    private final Area.Polygonal BAR_DISPENSER_AREA = polygonal(
            new Coordinate(1941 + 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4962 - 1, 0),
            new Coordinate(1941 + 1, 4962 - 1, 0));

    public void doAction(BlastFurnaceActionHandler.BlastFurnaceAction action) {
        switch (action) {
            case OPEN_BANK:
                log.info("Trying to open bank");
                openBank();
                delayUntil(() -> Bank.isOpen(), 5000);
                break;
            case WAIT:
                log.info("Waiting");
                Execution.delay(656, 789);
                break;
            case WITHDRAW_ICE_GLOVES:
                withdraw(ICE_GLOVES, 1);
                delayUntil(() -> Inventory.contains(ICE_GLOVES), 5000);
                break;
            case WITHDRAW_GOLDSMITH_GAUNTLETS:
                log.info("Trying to withdraw " + GOLDSMITH_GAUNTLETS);
                withdraw(GOLDSMITH_GAUNTLETS, 1);
                delayUntil(() -> Inventory.contains(GOLDSMITH_GAUNTLETS), 5000);
                break;
            case WITHDRAW_GOLD_ORE:
                log.info("Trying to withdraw " + GOLD_ORE);
                Bank.getItems(GOLD_ORE).get(0).interact("Withdraw-All");
                delayUntil(() -> Inventory.contains(GOLD_ORE), 5000);
                break;
            case CLOSE_BANK:
                closeBank();
                delayUntil(() -> !Bank.isOpen(), 5000);
                break;
            case SET_CAMERA_PITCH:
                log.info("Trying to set correct camera pitch");
                setCameraPitch();
                break;
            case SET_CAMERA_YAW:
                log.info("Trying to set correct camera yaw");
                setCameraYaw();
                break;
            case ASK_FOREMAN_PERMISSION:
                log.info("Trying to ask for foreman's permission");
                throw new IllegalStateException("TODO!");
            case PUT_ORE_IN_CONVEYOR:
                log.info("Trying to put ore into conveyor");
                GameObjects.newQuery().names("Conveyor belt").results().forEach(e -> e.interact(
                        "Put-ore-on"));
                delayUntil(() -> !Inventory.contains(GOLD_ORE), 15000);
                break;
            case GO_TO_BAR_DISPENSER:
                log.info("Trying to go to bar dispenser");
                BAR_DISPENSER_AREA.getRandomCoordinate().click();
                delayUntil(() -> BAR_DISPENSER_AREA.contains(getLocal().getPosition()), 5000);
                break;
            case TAKE_BAR_DISPENSER:
                log.info("Trying to take bars from bar dispenser");
                pressSpace();
                break;
            case EQUIP_ICE_GLOVES:
                log.info("Trying to equip " + ICE_GLOVES);
                Inventory.getItems(ICE_GLOVES).get(0).click();
                delayUntil(() -> Equipment.contains(ICE_GLOVES), 5000);
                break;
            case CLICK_ON_BAR_DISPENSER:
                log.info("Trying to click on bar dispenser");
                GameObjects.newQuery().names("Bar dispenser").results().get(0).click();
                delayUntil(ChatDialog::isOpen, 5000);
                break;
            case EQUIP_GOLDSMITH_GAUNTLETS:
                log.info("Trying to equip " + GOLDSMITH_GAUNTLETS);
                Inventory.getItems(GOLDSMITH_GAUNTLETS).get(0).click();
                delayUntil(() -> Equipment.contains(GOLDSMITH_GAUNTLETS), 5000);
                break;
            case DEPOSIT_GOLD_BARS:
                log.info("Trying to deposit "+ GOLD_BAR);
                Bank.depositAllExcept(ICE_GLOVES, GOLDSMITH_GAUNTLETS, "Coins");
                delayUntil(() -> !Equipment.contains(GOLD_BAR), 5000);
                break;
            default:
                throw new IllegalArgumentException("Unknown ACTION-" + action);
        }
    }

    private void openBank() {
        if (!Bank.isOpen()) {
            GameObjects.newQuery().names("Bank chest").results().get(0).click();

        }
    }

    private void closeBank() {
        log.info("Trying to close bank");
        if (!Bank.isOpen()) {
            log.warn("Tried to close bank when it was not opened");
            return;
        }
        Bank.close();
    }

    private void withdraw(String item, int amount) {
        if (!Bank.isOpen()) {
            log.warn("Tried to withdraw item from closed bank");
            return;
        }
        Bank.withdraw(item, 1);
    }

    private void setCameraPitch() {
        press(Button.WHEEL);
        while (isCameraPitchIncorrect()) {
            move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(-3, 3)),
                    (int) (Mouse.getPosition().getY() + Random.nextInt(60, 90))));
        }
        release(Button.WHEEL);
    }

    private boolean isCameraPitchIncorrect() {
        return Camera.getPitch() < 0.96;
    }

    private void pressSpace() {
        int spaceKey = 32;
        log.info("Pressing space to make wine");
        pressKey(spaceKey);
        delay(24, 39);
        releaseKey(spaceKey);
    }

    private void setCameraYaw() {
        press(Button.WHEEL);
        while (isCameraYawIncorrect()) {
            move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(40, 60)),
                    (int) (Mouse.getPosition().getY() + Random.nextInt(-5, 5))));
        }
        release(Button.WHEEL);
    }

    private boolean isCameraYawIncorrect() {
        return Camera.getYaw() < 261 || Camera.getYaw() > 299;
    }

    public enum BlastFurnaceAction {
        OPEN_BANK,
        WAIT,
        WITHDRAW_ICE_GLOVES,
        WITHDRAW_GOLDSMITH_GAUNTLETS,
        CLOSE_BANK,
        SET_CAMERA_PITCH,
        SET_CAMERA_YAW,
        ASK_FOREMAN_PERMISSION,
        PUT_ORE_IN_CONVEYOR,
        GO_TO_BAR_DISPENSER,
        TAKE_BAR_DISPENSER,
        EQUIP_ICE_GLOVES,
        CLICK_ON_BAR_DISPENSER,
        EQUIP_GOLDSMITH_GAUNTLETS,
        GO_TO_NEAR_BANK,
        WITHDRAW_GOLD_ORE,
        DEPOSIT_GOLD_BARS
    }
}
