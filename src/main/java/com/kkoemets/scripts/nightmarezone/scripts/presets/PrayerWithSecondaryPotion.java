package com.kkoemets.scripts.nightmarezone.scripts.presets;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.queries.results.QueryResults;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.osrs.local.hud.interfaces.Prayer;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class PrayerWithSecondaryPotion extends AbstractNightmareZoneScript {
    private final ThresholdContainer prayerThresholdContainer =
            new GenericThresholdContainerImpl(9, 26);


    public PrayerWithSecondaryPotion(BotLogger log) {
        super(log);
    }

    protected abstract String getPrayerRestoringPotionName();

    protected abstract String getSecondaryPotionName();

    protected abstract boolean drinkSecondaryPotionWithValidations(SpriteItemQueryResults rangePotions);

    @Override
    boolean doAdditionalValidations() {
        if (Stream.of(getPrayerRestoringPotions(), getSecondaryPotions())
                .allMatch(QueryResults::isEmpty)) {
            throw new IllegalStateException(format("No %s and %s",
                    getPrayerRestoringPotionName(), getSecondaryPotions()));
        }

        return true;
    }

    @Override
    boolean run() {
        log.info(getSecondaryPotionName() + " amount: " + getSecondaryPotions().size());
        if (getPrayerRestoringPotions().isEmpty()) {
            throw new RuntimeException("No more " + getPrayerRestoringPotionName());
        }

        log.info(getPrayerRestoringPotionName() + " amount: " + getPrayerRestoringPotions().size());

        int prayerPoints = Prayer.getPoints();
        log.info("Prayer points: " + prayerPoints);

        int prayerThreshold = prayerThresholdContainer.getThreshold();
        log.info("Current prayer prayerThreshold: " + prayerThreshold);
        if (prayerPoints <= prayerThreshold) {
            log.info("Drinking " + getPrayerRestoringPotionName());
            getPrayerRestoringPotions().get(0).click();
            prayerThresholdContainer.nextThreshold();
        }

        return validateRangeAndDrinkRangingPotionIfNecessary(getSecondaryPotions());
    }

    private boolean validateRangeAndDrinkRangingPotionIfNecessary(SpriteItemQueryResults rangePotions) {
        if (!Equipment.contains("Rune arrow")) {
            throw new RuntimeException("Missing rune arrows");
        }

        if (!Equipment.contains("Magic shortbow (i)")) {
            throw new RuntimeException("Missing Magic shortbow (i)");
        }

        return drinkSecondaryPotionWithValidations(rangePotions);
    }

    private SpriteItemQueryResults getPrayerRestoringPotions() {
        return getPotions(getPrayerRestoringPotionName());
    }

    protected SpriteItemQueryResults getSecondaryPotions() {
        return getPotions(getSecondaryPotionName());
    }

}
