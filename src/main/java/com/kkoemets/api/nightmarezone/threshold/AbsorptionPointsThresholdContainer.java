package com.kkoemets.api.nightmarezone.threshold;

import com.runemate.game.api.hybrid.util.calculations.Random;

public class AbsorptionPointsThresholdContainer implements ThresholdContainer {

    private int absorptionPointsThreshold;

    public AbsorptionPointsThresholdContainer() {
        nextThreshold();
    }

    @Override
    public void nextThreshold() {
        absorptionPointsThreshold = Random.nextInt(654, 789);
    }

    @Override
    public int getThreshold() {
        return absorptionPointsThreshold;
    }
}