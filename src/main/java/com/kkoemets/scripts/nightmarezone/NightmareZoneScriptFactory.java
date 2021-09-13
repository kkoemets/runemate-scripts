package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.nightmarezone.scripts.AbsorptionAndOverloadModeScript;
import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import com.runemate.game.api.script.framework.logger.BotLogger;

public class NightmareZoneScriptFactory {

    public static AbstractNightmareZoneScript overloadModeWithForDef1Pures(BotLogger log) {
        return new AbsorptionAndOverloadModeScript(log) {
            private final ThresholdContainer localHpThreshold = new GenericThresholdContainerImpl(2, 4);

            @Override
            protected boolean doAdditionalValidations() {
                if (!super.validate()) {
                    return false;
                }

                if (!hasOverloadPotionEnded() && isHpGreaterThan(localHpThreshold.getThreshold())) {
                    guzzleRockCakeUntilHpIs(1);
                    localHpThreshold.nextThreshold();
                }

                return true;
            }

            @Override
            public boolean execute() {
                return super.execute();
            }
        };
    }

}
