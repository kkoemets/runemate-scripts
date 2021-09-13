package com.kkoemets.scripts.nightmarezone.scripts;

import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Prayer;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.script.Execution.delay;

abstract class SuperRestoreAndRangingPotionMode extends AbstractNightmareZoneScript {
    protected static final String SUPER_RESTORE = "Super restore";
    protected static final String RANGING_POTION = "Ranging potion";

    protected SuperRestoreAndRangingPotionMode(BotLogger log) {
        super(log);
    }

    @Override
    boolean doAdditionalValidations() {
        return false;
    }

    @Override
    boolean run() {
        SpriteItemQueryResults rangePotions = getRangingPotions();
        SpriteItemQueryResults superRestorePotions = getSuperRestorePotions();

        log.info(RANGING_POTION + " amount: " + rangePotions.size());
        if (superRestorePotions.isEmpty()) {
            throw new RuntimeException("No more " + SUPER_RESTORE);
        }

        log.info(SUPER_RESTORE + " amount: " + superRestorePotions.size());

        int prayerPoints = Prayer.getPoints();
        log.info("Prayer points: " + prayerPoints);

        int prayerThreshold = prayerThresholdContainer.getThreshold();
        log.info("Current prayer prayerThreshold: " + prayerThreshold);
        if (prayerPoints <= prayerThreshold) {
            log.info("Drinking " + SUPER_RESTORE);
            superRestorePotions.get(0).click();
            prayerThresholdContainer.nextThreshold();
        }

        return validateRangeAndDrinkRangingPotionIfNecessary(rangePotions);
    }

    private boolean validateRangeAndDrinkRangingPotionIfNecessary(SpriteItemQueryResults rangePotions) {
        if (!Equipment.contains("Rune arrow")) {
            throw new RuntimeException("Missing rune arrows");
        }

        if (!Equipment.contains("Magic shortbow (i)")) {
            throw new RuntimeException("Missing Magic shortbow (i)");
        }

        int currentLevel = Skill.RANGED.getCurrentLevel();
        int baseLevel = Skill.RANGED.getBaseLevel();
        int dif = currentLevel - baseLevel;
        int rangingThreshold = rangeBuffThresholdContainer.getThreshold();

        log.info("Current ranging level: " + currentLevel);
        log.info("Base ranging level: " + baseLevel);
        log.info("Current ranging buff " + dif);
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

        log.info("Drinking " + RANGING_POTION);
        if (Random.nextInt(0, 100) >= 56 && !InterfaceWindows.getSkills().isOpen()) {
            log.info("Checking skills interface");
            InterfaceWindows.getSkills().open();
            delay(1245, 2356);
        }

        getRangingPotions().get(0).click(); // todo!!
        rangeBuffThresholdContainer.nextThreshold();

        return true;
    }

    protected SpriteItemQueryResults getSuperRestorePotions() {
        return getPotions(SUPER_RESTORE);
    }

    protected SpriteItemQueryResults getRangingPotions() {
        return getPotions(RANGING_POTION);
    }

}
