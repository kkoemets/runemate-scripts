package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.nightmarezone.scripts.AbsorptionAndOverloadModeScript;
import com.kkoemets.scripts.nightmarezone.scripts.AbsorptionModeScript;
import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import com.kkoemets.scripts.nightmarezone.scripts.SuperRestoreAndRangingPotionMode;
import com.runemate.game.api.script.framework.logger.BotLogger;

final class NightmareZoneScriptFactory {

    private NightmareZoneScriptFactory() {
    }

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
        };
    }

    public static AbstractNightmareZoneScript absorptionAndOverloadModeScript(BotLogger logger) {
        return new AbsorptionAndOverloadModeScript(logger) {
            @Override
            protected boolean doAdditionalValidations() {
                return super.doAdditionalValidations();
            }
        };
    }

    public static AbstractNightmareZoneScript superRestoreAndRangingPotionMode(BotLogger logger) {
        return new SuperRestoreAndRangingPotionMode(logger) {
        };
    }

    public static AbstractNightmareZoneScript absorptionModeScript(BotLogger logger) {
        return new AbsorptionModeScript(logger) {
        };
    }

}
