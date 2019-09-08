package com.kkoemets.scripts;

import com.kkoemets.api.common.interaction.InteractionHandler;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Area.Polygonal;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.kkoemets.scripts.BlastFurnace.BlastFurnaceAction.*;
import static com.kkoemets.scripts.BlastFurnace.BlastFurnaceScriptState.*;
import static com.runemate.game.api.hybrid.input.Mouse.*;
import static com.runemate.game.api.hybrid.location.Area.polygonal;
import static com.runemate.game.api.hybrid.region.Players.getLocal;

public class BlastFurnace extends LoopingBot implements MoneyPouchListener {

    private final Coordinate BANKING_TILE = new Coordinate(1948, 4957, 0);
    private final Coordinate AT_CONVERYOR_TILE = new Coordinate(1942, 4967, 0);
    private final Area.Polygonal BAR_DISPENSER_AREA = polygonal(
            new Coordinate(1941 + 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4964 + 1, 0),
            new Coordinate(1939 - 1, 4962 - 1, 0),
            new Coordinate(1941 + 1, 4962 - 1, 0));
    private final String GOLDSMITH_GAUNTLETS = "Goldsmith gauntlets";
    private final String ICE_GLOVES = "Ice gloves";
    private final String GOLD_ORE = "Gold ore";
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
//        log.info(Camera.getZoom()); // 0
//        log.info(Camera.getYaw());
//        log.debug(getLocal().getPosition());
//        if ((Camera.getYaw() < 273 || Camera.getYaw() > 279) && Camera.getPitch() < 0.96) {
//            MouseWheel.mouseWheelTurnTo(new Coordinate(getLocal().getPosition().getX() + 16, 4960,
//                            0),
//                    getLocal());
//            delay(4546, 7600);
//
//        }
//
//        press(Mouse.Button.WHEEL);
//        if ((Camera.getYaw() < 273 || Camera.getYaw() > 279) && Camera.getPitch() < 0.96) {
//            move(new InteractablePoint((int) (Mouse.getPosition().getX() + 80),
//                    (int) (Mouse.getPosition().getY() + Random.nextInt(40, 60))));
//            Execution.delay(124, 196);
//            release(Mouse.Button.WHEEL);
//            log.info("Pitch-" + Camera.getPitch());
//        }

//        setCamera();

//        Execution.delay(1000, 1200);
//
//        openBank();
//
//        GameObjects.newQuery().names("Conveyor belt").results().get(0).click();
//
//        GameObjects.newQuery().names("Bar dispenser").results().get(0);
//
//        Interfaces.getSelected().isVisible();
//
//        checkForemanPermission();

//        Camera.turnTo(0.95);
//
//        log.info("Pitch-" + Camera.getPitch());
//        log.info("Yaw-" + Camera.getYaw());
//
//        if (Camera.getZoom() > Random.nextDouble(0.051, 0.082)) {
//            log.debug("hi");
//            Camera.setZoom(0.0275, 0.0225);
//        }
        log.info(getLocal().getPosition());

        BlastFurnaceAction action = decideNextAction();
        log.info("Decided next action-" + action);
        switch (action) {
            case OPEN_BANK:
                log.info("Trying to open bank");
                break;
            case WAIT:
                log.info("Waiting");
                break;
            case WITHDRAW_ICE_GLOVES:
                log.info("Trying to withdraw " + ICE_GLOVES);
                break;
            case WITHDRAW_GOLDSMITH_GAUNTLETS:
                log.info("Trying to withdraw " + GOLDSMITH_GAUNTLETS);
                break;
            case CLOSE_BANK:
                log.info("Trying to close bank");
                break;
            case SET_CAMERA_PITCH:
                log.info("Trying to set correct camera pitch");
                break;
            case SET_CAMERA_YAW:
                log.info("Trying to set correct camera yaw");
                break;
            case ASK_FOREMAN_PERMISSION:
                log.info("Trying to ask for foreman's permission");
                break;
            case PUT_ORE_IN_CONVEYOR:
                log.info("Trying to put ore into conveyor");
                break;
            case GO_TO_BAR_DISPENSER:
                log.info("Trying to go to bar dispenser");
                break;
            case TAKE_BAR_DISPENSER:
                log.info("Trying to take bars from bar dispenser");
                break;
            case EQUIP_ICE_GLOVES:
                log.info("Trying to equip " + ICE_GLOVES);
                break;
            case CLICK_ON_BAR_DISPENSER:
                log.info("Trying to click on bar dispenser");
            case EQUIP_GOLDSMITH_GAUNTLETS:
                log.info("Trying to equip " + GOLDSMITH_GAUNTLETS);
            default:
                throw new IllegalArgumentException("Unknown ACTION-" + action);
        }


    }

    private BlastFurnaceAction decideNextAction() {
        log.info("Starting to decide next state");

        if (Players.getLocal().getAnimationId() != -1) {
            log.info("Player is not idle");
            return BlastFurnaceAction.WAIT;
        }

        if (isCameraPitchIncorrect()) {
            log.info("Camera pitch-" + Camera.getPitch() + " is incorrect");
            return SET_CAMERA_PITCH;
        }

        if (isCameraYawIncorrect()) {
            log.info("Camera yaw-" + Camera.getYaw() + " is incorrect");
            return SET_CAMERA_YAW;
        }

        switch (getPlayerLocation()) {
            case NEAR_BANK:
                BlastFurnaceScriptState stateNearBank = decideNextStateNearBank();
                log.info("STATE-" + stateNearBank + " after deciding what to do near bank");
                switch (decideNextStateNearBank()) {
                    case IN_BANK:
                        return decideNextActionInBank();
                    case WANT_TO_OPEN_BANK:
                        log.info("Want to open bank");
                        return OPEN_BANK;
                    case NO_BANKING_NEEDED:
                        log.info("No banking needed");
                        return PUT_ORE_IN_CONVEYOR;
                    default:
                        throw new IllegalStateException("Illegal STATE-" + stateNearBank +
                                " after deciding what to do near bank");
                }
            case AT_CONVEYOR:
                log.info("Player is at conveyor");
                if (!playerHasIceGlovesInInventoryOrEquipment()) {
                    log.info("Player does not have " + ICE_GLOVES + " in inventory/equipment");
                    return OPEN_BANK;
                }

                if (!playerHasGoldsmithGauntletsInInventoryOrEquipment()) {
                    log.info("Player does not have " + GOLDSMITH_GAUNTLETS + " in inventory/equipment");
                    return OPEN_BANK;
                }

                if (isEquipped(GOLDSMITH_GAUNTLETS)) {
                    return EQUIP_GOLDSMITH_GAUNTLETS;
                }

                if (Inventory.getItems(GOLD_ORE).isEmpty()) {
                    log.info("Player does not have " + GOLD_ORE + " in inventory");
                    return GO_TO_BAR_DISPENSER;
                }

                if (ChatDialog.isOpen() && ChatDialog.getText().contains("permission")) {
                    log.info("Player needs foreman's permission");
                    return ASK_FOREMAN_PERMISSION;
                }
                return PUT_ORE_IN_CONVEYOR;
            case AT_BAR_DISPENSER:
                log.info("Player is at bar dispenser");
                if (!isEquipped(ICE_GLOVES)) {
                    log.info("Trying to equip " + ICE_GLOVES);
                    return EQUIP_ICE_GLOVES;
                }

                if (isTakeBarsDialogOpen()) {
                    log.info("Take bars dialog is open at bar dispenser");
                    return TAKE_BAR_DISPENSER;
                }
                if (ChatDialog.isOpen() && ChatDialog.getText().contains("from the dispenser")) {
                    log.info("Took bars from the dispenser");
                    return OPEN_BANK;
                }

                if (ChatDialog.isOpen() && ChatDialog.getText().contains("contain any bars")) {
                    log.warn("Tried to take bars from dispenser but it was empty");
                    return OPEN_BANK;
                }

                return CLICK_ON_BAR_DISPENSER;
            case AT_UNKNOWN_LOCATION:
                log.info("Player is at unknown location");
                return GO_TO_NEAR_BANK;
            default:
                throw new IllegalStateException("Unknown state after checking player's " +
                        "location");

        }
    }

    private Boolean isTakeBarsDialogOpen() {
        return Interfaces.newQuery().actions("Take").results().size() > 0;
    }

    private boolean isCameraYawIncorrect() {
        return Camera.getYaw() < 261 || Camera.getYaw() > 299;
    }

    private BlastFurnaceAction decideNextActionInBank() {
        if (!playerHasIceGlovesInInventoryOrEquipment()) {
            log.info("Player does not have Ice gloves in inventory/equipment");
            return WITHDRAW_ICE_GLOVES;
        }
        if (!playerHasGoldsmithGauntletsInInventoryOrEquipment()) {
            return WITHDRAW_GOLDSMITH_GAUNTLETS;
        }
        return CLOSE_BANK;
    }

    private BlastFurnaceScriptState decideNextStateNearBank() {
        log.info("Player is near bank");
        if (Bank.isOpen()) {
            log.info("Bank is open");
            return IN_BANK;
        }
        if (Inventory.getItems(GOLD_ORE).isEmpty()) {
            log.info("Player does not have any " + GOLD_ORE + " in inventory");
            return WANT_TO_OPEN_BANK;
        }
        if (!playerHasIceGlovesInInventoryOrEquipment()) {
            log.info("Player does not have " + ICE_GLOVES + " in inventory/equipment");
            return WANT_TO_OPEN_BANK;
        }

        if (!playerHasGoldsmithGauntletsInInventoryOrEquipment()) {
            log.info("Player does not have " + GOLDSMITH_GAUNTLETS + " in inventory/equipment");
            return WANT_TO_OPEN_BANK;
        }

        log.info("All conditions met");
        return NO_BANKING_NEEDED;
    }

    private boolean playerHasIceGlovesInInventoryOrEquipment() {
        return Inventory.getItems(ICE_GLOVES).size() == 1 ||
                isEquipped(ICE_GLOVES);
    }

    private boolean playerHasGoldsmithGauntletsInInventoryOrEquipment() {
        return Inventory.getItems(GOLDSMITH_GAUNTLETS).size() == 1 ||
                isEquipped(GOLDSMITH_GAUNTLETS);
    }

    private boolean isEquipped(String item) {
        return Equipment.contains(item);
    }

    private BlastFurnaceScriptState getPlayerLocation() {
        if (isPlayerNearBank()) {
            return NEAR_BANK;
        }
        if (isPlayerNearBarDispenser()) {
            return AT_BAR_DISPENSER;
        }
        if (isPlayerAtConveyor()) {
            return AT_CONVEYOR;
        }
        return AT_UNKNOWN_LOCATION;
    }

    private boolean isPlayerNearBarDispenser() {
        return BAR_DISPENSER_AREA.contains(getLocal().getPosition());
    }

    private boolean isPlayerAtConveyor() {
        return AT_CONVERYOR_TILE.getArea().contains(getLocal().getPosition());
    }

    private boolean isPlayerNearBank() {
        return getPolygonal(BANKING_TILE, 5).contains(getLocal().getPosition());
    }

    private Polygonal getPolygonal(Coordinate coordinate, int distanceFromCenter) {
        return polygonal(
                new Coordinate(coordinate.getX() - distanceFromCenter,
                        coordinate.getY() - distanceFromCenter, 0),
                new Coordinate(coordinate.getX() - distanceFromCenter,
                        coordinate.getY() + distanceFromCenter, 0),
                new Coordinate(coordinate.getX() + distanceFromCenter,
                        coordinate.getY() + distanceFromCenter, 0),
                new Coordinate(coordinate.getX() + distanceFromCenter,
                        coordinate.getY() - distanceFromCenter, 0));
    }

    private void openBank() {
        if (!Bank.isOpen()) {
            GameObjects.newQuery().names("Bank chest").results().get(0).click();

        }
    }

    private void setCamera() {
        press(Button.WHEEL);
        while (isCameraPitchIncorrect()) {
            move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(-3, 3)),
                    (int) (Mouse.getPosition().getY() + Random.nextInt(60, 90))));
        }
        release(Button.WHEEL);

        press(Button.WHEEL);
        while (isCameraYawIncorrect()) {
            move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(40, 60)),
                    (int) (Mouse.getPosition().getY() + Random.nextInt(-5, 5))));
        }
        release(Button.WHEEL);
    }

    private boolean isCameraPitchIncorrect() {
        return Camera.getPitch() < 0.96;
    }

    enum BlastFurnaceScriptState {
        NEAR_BANK,
        AT_CONVEYOR,
        AT_BAR_DISPENSER,
        AT_UNKNOWN_LOCATION,
        IN_BANK,
        WANT_TO_OPEN_BANK,
        NO_BANKING_NEEDED
    }

    enum BlastFurnaceAction {
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
        GO_TO_NEAR_BANK
    }
}