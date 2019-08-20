package com.kkoemets.api.nightmarezone.threshold;

import com.runemate.game.api.hybrid.util.calculations.Random;

public class HpThresholdContainer implements ThresholdContainer {

    private int hpThreshold;

    public HpThresholdContainer() {
        nextThreshold();
    }

    @Override
    public void nextThreshold() {
        hpThreshold = Random.nextInt(5, 9);
    }

    @Override
    public int getThreshold() {
        return hpThreshold;
    }

}