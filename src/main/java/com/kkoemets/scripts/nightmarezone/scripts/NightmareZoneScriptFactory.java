package com.kkoemets.scripts.nightmarezone.scripts;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.nightmarezone.scripts.presets.*;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;

import static com.kkoemets.scripts.nightmarezone.scripts.presets.ScriptName.*;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Arrays.asList;

public final class NightmareZoneScriptFactory {

    private NightmareZoneScriptFactory() {
    }

    public static List<AbstractNightmareZoneScript> getAll(BotLogger logger) {
        return asList(
                absorptionAndOverloadModeForDef1Pures(logger),
                absorptionAndOverloadModeScript(logger),
                superRestoreAndRangingPotionMode(logger),
                absorptionModeScript(logger),
                superRestoreAndOverloadMode(logger)
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
            public boolean execute() {
                super.execute();

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
        };
    }

    public static AbstractNightmareZoneScript superRestoreAndRangingPotionMode(BotLogger logger) {
        return new PrayerWithSecondaryPotion(logger) {
            protected final ThresholdContainer rangeBuffThresholdContainer =
                    new GenericThresholdContainerImpl(5, 8);

            @Override
            protected String getPrayerRestoringPotionName() {
                return SUPER_RESTORE;
            }

            @Override
            protected String getSecondaryPotionName() {
                return RANGING_POTION;
            }

            @Override
            protected boolean drinkSecondaryPotionWithValidations(SpriteItemQueryResults rangePotions) {
                int currentLevel = Skill.RANGED.getCurrentLevel();
                int baseLevel = Skill.RANGED.getBaseLevel();
                int dif = currentLevel - baseLevel;

                log.info("Current ranging level: " + currentLevel);
                log.info("Base ranging level: " + baseLevel);
                log.info("Current ranging buff " + dif);

                int rangingThreshold = rangeBuffThresholdContainer.getThreshold();
                log.info("Current ranging rangingThreshold " + rangingThreshold);
                if (rangePotions.isEmpty()) {
                    return true;
                }

                if (currentLevel - baseLevel > rangingThreshold) {
                    return true;
                }

                delay(5245, 12356);

                if (Skill.RANGED.getCurrentLevel() - Skill.RANGED.getBaseLevel() > rangingThreshold) {
                    return true;
                }

                if (rangePotions.isEmpty()) {
                    return true;
                }

                log.info("Drinking " + getSecondaryPotionName());
                if (Random.nextInt(0, 100) >= 56 && !InterfaceWindows.getSkills().isOpen()) {
                    log.info("Checking skills interface");
                    InterfaceWindows.getSkills().open();
                    delay(1245, 2356);
                }

                getSecondaryPotions().get(0).click(); // todo!!
                rangeBuffThresholdContainer.nextThreshold();

                return true;
            }

            @Override
            public ScriptName getScriptName() {
                return SUPER_RESTORE_AND_RANGING_POTION_MODE;
            }
        };
    }

    public static AbstractNightmareZoneScript superRestoreAndOverloadMode(BotLogger logger) {
        return new PrayerWithSecondaryPotion(logger) {
            @Override
            protected String getPrayerRestoringPotionName() {
                return SUPER_RESTORE;
            }

            @Override
            protected String getSecondaryPotionName() {
                return OVERLOAD;
            }

            @Override
            protected boolean drinkSecondaryPotionWithValidations(
                    SpriteItemQueryResults rangePotions) {
                if (getOverloadPotions().isEmpty()) {
                    return true;
                }

                drinkOverloadDose();
                return true;
            }

            @Override
            public ScriptName getScriptName() {
                return SUPER_RESTORE_AND_OVERLOAD_MODE;
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
