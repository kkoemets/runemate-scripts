package com.kkoemets.scripts.nightmarezone.scripts;

import com.runemate.game.api.script.framework.logger.BotLogger;

import static java.math.BigInteger.ONE;

public abstract class AbsorptionModeScript extends AbstractNightmareZoneScript {

    protected AbsorptionModeScript(BotLogger log) {
        super(log);
    }

    @Override
    boolean doAdditionalValidations() {
        return validateRockCake();
    }

    @Override
    boolean run() {
        if (!isHpGreaterThan(hpThresholdContainer.getThreshold())) {
            return true;
        }

        if (getAbsorptionPotions().isEmpty()) {
            return true;
        }

        if (getAbsorptionPoints().isPresent() && !isAbsorptionPointsUnder(getAbsorptionPoints().get(),
                absorptionPointsThreshold.getThreshold())) {
            return true;
        }

        log.info("Hp is below threshold: " + hpThresholdContainer.getThreshold());
        guzzleRockCakeUntilHpIs(ONE.intValue());
        hpThresholdContainer.nextThreshold();
        log.info("New hp threshold: " + hpThresholdContainer.getThreshold());

        log.info("Absorption potion is below threshold: " + absorptionPointsThreshold.getThreshold());
        drinkAbsorptionPotionsUntilFull();
        absorptionPointsThreshold.nextThreshold();
        log.info("New absorption potion threshold: " + absorptionPointsThreshold.getThreshold());

        return true;
    }

}


