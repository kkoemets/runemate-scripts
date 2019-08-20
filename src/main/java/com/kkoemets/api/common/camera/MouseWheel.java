package com.kkoemets.api.common.camera;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.util.calculations.Random;

import static com.runemate.game.api.hybrid.input.Mouse.*;

public class MouseWheel {

    private MouseWheel() {
    }

    public static void mouseWheelTurnTo(Coordinate target, Player player) {
        Coordinate Pc = player.getPosition();

        int Dx = target.getX() - Pc.getX();
        int Dy = target.getY() - Pc.getY();
        int Yaw = Camera.getYaw();

        int Beta = (int) (Math.atan2(-Dx, Dy) * 180 / Math.PI); //atan2 is in radians so 180/PI wil transform it to degrees, like the game.
        if (Beta < 0) Beta = 360 + Beta;

        int deltaYaw = Beta - Yaw;

        if (deltaYaw > 180) {
            deltaYaw = deltaYaw - 360;
        } else if (deltaYaw < -180) {
            deltaYaw = deltaYaw + 360;
        }

        int deltaMouseMoveX = (int) (-deltaYaw * 2.5);

        Area hoverArea = new Area.Circular(player.getPosition(), 3);
        hoverArea.getRandomCoordinate().hover();

        press(Mouse.Button.WHEEL);
        move(new InteractablePoint((int) (Mouse.getPosition().getX() + deltaMouseMoveX), (int) (Mouse.getPosition().getY() + Random.nextInt(-10, 10))));
        release(Mouse.Button.WHEEL);
    }
}