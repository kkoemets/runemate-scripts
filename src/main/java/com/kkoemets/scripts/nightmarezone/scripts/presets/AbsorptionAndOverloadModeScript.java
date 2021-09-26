package com.kkoemets.scripts.nightmarezone.scripts.presets;

import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.script.Execution.delayWhile;

public abstract class AbsorptionAndOverloadModeScript extends AbsorptionModeScript {
    protected AbsorptionAndOverloadModeScript(BotLogger log) {
        super(log);
    }

    @Override
    public boolean execute() {
        if (getOverloadPotions().isEmpty()) {
            return super.execute();
        }

        if (getOverloadTime().isPresent() && !hasOverloadPotionEnded()) {
            return true;
        }

        if (isHpGreaterThan(50)) {
            log.info("Drinking overload potion");
            drinkOverloadDose();
        }

        if (!getAbsorptionPotions().isEmpty()) {
            log.info("Drinking absorption potions until full");
            drinkAbsorptionPotionsUntilFull();
        }

        delayWhile(() -> getOverloadTime().get().getValue() > 19, 5757, 7213);
        log.info("Guzzling dwarven rock cake until full");
        guzzleRockCakeUntilHpIs(1);

        log.debug("End of main loop, overload time approx. left: " + (getOverloadTime().isPresent() ?
                getOverloadTime().get().getValue() * 15 : 0) + " seconds");

        return true;
    }

    @Override
    protected void guzzleRockCakeUntilHpIs(int i) {
        guzzleRockCakeUntilHpIs(1, () -> getOverloadPotions().isEmpty() || !hasOverloadPotionEnded());
    }

}