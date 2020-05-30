package com.kkoemets.api.nightmarezone.threshold;

import com.runemate.game.api.hybrid.util.calculations.Random;

public class GenericThresholdContainerImpl implements ThresholdContainer {

    private int threshold;
    private int min;
    private int max;

    public GenericThresholdContainerImpl(int min, int max) {
        this.min = min;
        this.max = max;
        nextThreshold();
    }

    @Override
    public void nextThreshold() {
        threshold = Random.nextInt(min, max);
    }

    @Override
    public int getThreshold() {
        return threshold;
    }

}
