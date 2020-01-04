package com.kkoemets.scripts.blastfurnace.camera;

import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;
import com.runemate.game.api.hybrid.util.calculations.Random;

import static com.runemate.game.api.hybrid.input.Mouse.Button.WHEEL;
import static com.runemate.game.api.hybrid.input.Mouse.*;
import static com.runemate.game.api.hybrid.local.Camera.getPitch;
import static com.runemate.game.api.hybrid.local.Camera.getYaw;

public class BlastFurnaceCameraConfigurer {

    private BlastFurnaceCameraConfigurer() {
    }

    public static boolean setCamera() {
        if (isCameraPitchIncorrect()) {
            press(WHEEL);
            while (isCameraPitchIncorrect()) {
                move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(-3, 3)),
                        (int) (Mouse.getPosition().getY() + Random.nextInt(60, 90))));
            }
            release(WHEEL);
            return setCamera();
        }

        if (isCameraYawIncorrect()) {
            press(WHEEL);
            while (isCameraYawIncorrect()) {
                move(new InteractablePoint((int) (Mouse.getPosition().getX() + Random.nextInt(40, 60)),
                        (int) (Mouse.getPosition().getY() + Random.nextInt(-5, 5))));
            }
            release(WHEEL);
            return setCamera();
        }

        return true;
    }

    public static boolean cameraIsNotSet() {
        return isCameraPitchIncorrect() || isCameraYawIncorrect();
    }


    private static boolean isCameraPitchIncorrect() {
        return getPitch() < 0.96;
    }

    private static boolean isCameraYawIncorrect() {
        return getYaw() < 261 || getYaw() > 299;
    }

}