package com.kkoemets.scripts.blastfurnace;

import com.kkoemets.scripts.blastfurnace.BlastFurnaceActionHandler.BlastFurnaceAction;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.*;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceActionHandler.BlastFurnaceAction.*;
import static com.kkoemets.scripts.blastfurnace.BlastFurnaceStateDecider.BlastFurnaceScriptState.*;
import static com.runemate.game.api.hybrid.location.Area.polygonal;
import static com.runemate.game.api.hybrid.region.Players.getLocal;

public class BlastFurnaceStateDecider {

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
    private final String GOLD_BAR = "Gold bar";
    private BotLogger log;

    public BlastFurnaceStateDecider(BotLogger log) {
        this.log = log;
    }

    public BlastFurnaceAction decideNextAction() {
        log.info("Starting to decide next state");

        if (Players.getLocal().getAnimationId() != -1) {
            log.info("Player is not idle");
            return WAIT;
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
                        if (!Equipment.contains(GOLDSMITH_GAUNTLETS)) {
                            return EQUIP_GOLDSMITH_GAUNTLETS;
                        }
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
                    log.info("Player does not have " + GOLDSMITH_GAUNTLETS +
                            " in inventory/equipment");
                    return OPEN_BANK;
                }

                if (Inventory.getItems(GOLD_ORE).isEmpty()) {
                    log.info("Player does not have " + GOLD_ORE + " in inventory");
                    if (!Equipment.contains(ICE_GLOVES)) {
                        log.info("Trying to equip " + ICE_GLOVES);
                        return EQUIP_ICE_GLOVES;
                    }
                    return CLICK_ON_BAR_DISPENSER;
                }

                if (ChatDialog.isOpen() && ChatDialog.getText().contains("permission")) {
                    log.info("Player needs foreman's permission");
                    return ASK_FOREMAN_PERMISSION;
                }
                return PUT_ORE_IN_CONVEYOR;
            case AT_BAR_DISPENSER:
                log.info("Player is at bar dispenser");

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

        if (Inventory.contains(GOLD_BAR)) {
            return DEPOSIT_GOLD_BARS;
        }

        if (Inventory.getItems(GOLD_ORE).isEmpty()) {
            return WITHDRAW_GOLD_ORE;
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


    private boolean playerHasIceGlovesInInventoryOrEquipment() {
        return Inventory.getItems(ICE_GLOVES).size() == 1 ||
                Equipment.contains(ICE_GLOVES);
    }

    private boolean playerHasGoldsmithGauntletsInInventoryOrEquipment() {
        return Inventory.getItems(GOLDSMITH_GAUNTLETS).size() == 1 ||
                Equipment.contains(GOLDSMITH_GAUNTLETS);
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

    private Area.Polygonal getPolygonal(Coordinate coordinate, int distanceFromCenter) {
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

    private boolean isCameraPitchIncorrect() {
        return Camera.getPitch() < 0.96;
    }

    public enum BlastFurnaceScriptState {
        NEAR_BANK,
        AT_CONVEYOR,
        AT_BAR_DISPENSER,
        AT_UNKNOWN_LOCATION,
        IN_BANK,
        WANT_TO_OPEN_BANK,
        NO_BANKING_NEEDED
    }

}