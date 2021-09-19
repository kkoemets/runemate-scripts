package com.kkoemets.scripts.nightmarezone.scripts.presets;

import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.script.Execution.delayWhile;
import static java.util.Optional.ofNullable;

public abstract class AbsorptionAndOverloadModeScript extends AbsorptionModeScript {
    private static final String OVERLOAD = "Overload ";

    protected AbsorptionAndOverloadModeScript(BotLogger log) {
        super(log);
    }

    @Override
    public boolean doAdditionalValidations() {
        return super.doAdditionalValidations();
    }

    @Override
    boolean run() {
        if (getOverloadPotions().isEmpty()) {
            return super.validate() && super.run();
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

    protected void drinkOverloadDose() {
        if (getOverloadTime().isPresent() && !hasOverloadPotionEnded()) {
            return;
        }
        getOverloadPotions().sortByIndex().get(0).click();
        delayWhile(() -> !getOverloadTime().isPresent() || getOverloadTime().get().getValue() != 20,
                3533, 5345);
        drinkOverloadDose();
    }

    protected SpriteItemQueryResults getOverloadPotions() {
        return getPotions(OVERLOAD);
    }


    protected Optional<Varbit> getOverloadTime() {
        int overloadVarBit = 3955;
        return ofNullable(load(overloadVarBit));
    }

    protected boolean hasOverloadPotionEnded() {
        return getOverloadTime()
                .map(time -> time.getValue() == 0)
                .orElse(true);
    }

}