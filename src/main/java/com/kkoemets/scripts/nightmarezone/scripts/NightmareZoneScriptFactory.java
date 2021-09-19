package com.kkoemets.scripts.nightmarezone.scripts;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import com.kkoemets.scripts.nightmarezone.scripts.presets.*;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;

import static com.kkoemets.scripts.nightmarezone.scripts.presets.ScriptName.*;
import static java.util.Arrays.asList;

public final class NightmareZoneScriptFactory {

    private NightmareZoneScriptFactory() {
    }

    public static List<AbstractNightmareZoneScript> getAll(BotLogger logger) {
        return asList(
                absorptionAndOverloadModeForDef1Pures(logger),
                absorptionAndOverloadModeScript(logger),
                superRestoreAndRangingPotionMode(logger),
                absorptionModeScript(logger)
        );
    }

    public static AbstractNightmareZoneScript absorptionAndOverloadModeForDef1Pures(BotLogger log) {
        return new AbsorptionAndOverloadModeScript(log) {
            private final ThresholdContainer localHpThreshold = new GenericThresholdContainerImpl(2, 4);

            @Override
            public ScriptName getScriptName() {
                return ABSORPTION_AND_OVERLOAD_MODE_FOR_DEF_1_PURES;
            }

            @Override
            public boolean doAdditionalValidations() {
                super.doAdditionalValidations();

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
            public ScriptName getScriptName() {
                return ABSORPTION_AND_OVERLOAD_MODE_SCRIPT;
            }

            @Override
            public boolean doAdditionalValidations() {
                return super.doAdditionalValidations();
            }
        };
    }

    public static AbstractNightmareZoneScript superRestoreAndRangingPotionMode(BotLogger logger) {
        return new SuperRestoreAndRangingPotionMode(logger) {
            @Override
            public ScriptName getScriptName() {
                return SUPER_RESTORE_AND_RANGING_POTION_MODE;
            }
        };
    }

    public static AbstractNightmareZoneScript absorptionModeScript(BotLogger logger) {
        return new AbsorptionModeScript(logger) {
            @Override
            public ScriptName getScriptName() {
                return ABSORPTION_MODE_SCRIPT;
            }
        };
    }

}
