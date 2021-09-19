package com.kkoemets.scripts.nightmarezone.scripts;

import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.script.Execution.delay;
import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;

public abstract class AbsorptionModeScript extends AbstractNightmareZoneScript {
    private static final String DWARVEN_ROCK_CAKE = "Dwarven rock cake";
    private static final String ABSORPTION = "Absorption ";

    protected AbsorptionModeScript(BotLogger log) {
        super(log);
    }

    @Override
    public boolean doAdditionalValidations() {
        return validateRockCake();
    }

    @Override
    public boolean execute() {
        return validate() && doAdditionalValidations() && run();
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

    private boolean validateRockCake() {
        if (getDwarvenRockCake().isEmpty()) {
            throw new IllegalStateException("Player does not have a rock cake!");
        }

        if (getDwarvenRockCake().get(0).getIndex() < 24) {
            throw new IllegalStateException("Dwarven rock cake must in the last row in inventory");
        }

        return true;
    }

    private SpriteItemQueryResults getDwarvenRockCake() {
        return Inventory.getItems(DWARVEN_ROCK_CAKE);
    }

    private void guzzleRockCake() throws IllegalStateException {
        getDwarvenRockCake().get(0).interact("Guzzle");
        delay(Random.nextInt(190, 330) / 3);
    }

    protected SpriteItemQueryResults getAbsorptionPotions() {
        return getPotions(ABSORPTION);
    }

    private void drinkAbsorptionPotion() {
        getAbsorptionPotions().get(0).click();
        delay(Random.nextInt(190, 330));
    }


    protected void drinkAbsorptionPotionsUntilFull() {
        while (!getAbsorptionPoints().isPresent() ||
                (isAbsorptionPointsUnderMax(getAbsorptionPoints().get()) && !getAbsorptionPotions()
                        .isEmpty())) {
            drinkAbsorptionPotion();
        }
    }

    protected void guzzleRockCakeUntilHpIs(int i) {
        while (Health.getCurrent() != i) {
            guzzleRockCake();
        }
    }

    private boolean isAbsorptionPointsUnderMax(Varbit absorptionPoints) {
        return isAbsorptionPointsUnder(absorptionPoints, 951);
    }

    private Optional<Varbit> getAbsorptionPoints() {
        return ofNullable(load(NamedVarbit.NMZ_ABSORPTION.getId()));
    }

    private boolean isAbsorptionPointsUnder(Varbit varbit, int i) {
        return varbit.getValue() < i;
    }

}