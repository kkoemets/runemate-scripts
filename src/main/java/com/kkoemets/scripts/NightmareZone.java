package com.kkoemets.scripts;

import com.kkoemets.api.nightmarezone.NightmareZoneStateContainer;
import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceWindows;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.osrs.local.hud.interfaces.Prayer;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.kkoemets.api.nightmarezone.NightmareZoneStateContainer.DreamMode.*;
import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static com.runemate.game.api.script.Execution.delayWhile;
import static java.math.BigInteger.ONE;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class NightmareZone extends LoopingBot implements MoneyPouchListener {

    private static final String SUPER_RESTORE = "Super restore";
    private static final String RANGING_POTION = "Ranging potion";
    private static final String ABSORPTION = "Absorption ";
    private static final String OVERLOAD = "Overload ";
    private static final String DWARVEN_ROCK_CAKE = "Dwarven rock cake";
    private String aSetting;
    private BotLogger log;

    private ThresholdContainer hpThresholdContainer;
    private ThresholdContainer absorptionPointsThreshold;
    private ThresholdContainer prayerThresholdContainer;
    private ThresholdContainer rangeBuffThresholdContainer;
    private NightmareZoneStateContainer nightmareZoneStateContainer;

    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public NightmareZone() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((int) (ACTIVENESS_FACTOR_WHILE_WAITING.getAsDouble() * 10000));
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();

        nightmareZoneStateContainer = new NightmareZoneStateContainer(ABSORPTION_AND_OVERLOAD_MODE);

        hpThresholdContainer = new GenericThresholdContainerImpl(2, 4);
        absorptionPointsThreshold = new GenericThresholdContainerImpl(769, 821);
        rangeBuffThresholdContainer = new GenericThresholdContainerImpl(5, 8);
        prayerThresholdContainer = new GenericThresholdContainerImpl(9, 26);
//        setZoom(0.027, 0.004);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        Optional<Player> player = ofNullable(getLocal());
        if (!player.isPresent()) {
            log.info("Player is not visible yet, waiting...");
            return;
        }

        if (!isPlayerInADream(player.get())) {
            throw new IllegalStateException("Player is not in a dream!");
        }

        NightmareZoneStateContainer.DreamMode dreamMode = nightmareZoneStateContainer.DREAM_MODE();
        if (asList(ABSORPTION_AND_RANGING_MODE, ABSORPTION_MODE, ABSORPTION_AND_OVERLOAD_MODE).contains(dreamMode)) {
            validateRockCake();
        }

        switch (dreamMode) {
            case ABSORPTION_MODE:
                absorptionModeScript();
                break;
            case ABSORPTION_AND_OVERLOAD_MODE:
                absorptionAndOverloadModeScript();
                break;
            case SUPER_RESTORE_AND_RANGING_MODE:
                superRestoreAndRangerPotionMode();
                break;
            case ABSORPTION_AND_RANGING_MODE:
                absorptionAndRangingMode();
        }

        log.info("The end");
    }

    private boolean absorptionAndOverloadModeScript() {
        if (getOverloadPotions().isEmpty()) {
            return absorptionModeScript();
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

    private boolean absorptionModeScript() {
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

    private boolean superRestoreAndRangerPotionMode() {
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

    private boolean absorptionAndRangingMode() {
        Optional<Varbit> absorptionPoints = getAbsorptionPoints();
        absorptionPoints.ifPresent(points -> log.info("Current absorption points: " + points.getValue()));
        log.info("Current absorption points threshold: " + absorptionPointsThreshold.getThreshold());

        if (getAbsorptionPotions().isEmpty()) {
            log.info("Current hp threshold: " + hpThresholdContainer.getThreshold());
        }

        if (getAbsorptionPotions().isEmpty() && isHpGreaterThan(hpThresholdContainer.getThreshold())) {
            guzzleRockCakeUntilHpIs(ONE.intValue());
            hpThresholdContainer.getThreshold();
        }

        if ((!absorptionPoints.isPresent() || isAbsorptionPointsUnder(absorptionPoints.get(),
                absorptionPointsThreshold.getThreshold())) && !getAbsorptionPotions().isEmpty()) {
            log.info("Absorption potion is below threshold: " + absorptionPointsThreshold.getThreshold());
            drinkAbsorptionPotionsUntilFull();

            absorptionPointsThreshold.nextThreshold();
            log.info("New absorption potion threshold: " + absorptionPointsThreshold.getThreshold());

            log.info("Starting to guzzle dwarven rock cake until hp is 1");
            guzzleRockCakeUntilHpIs(ONE.intValue());
        }

        SpriteItemQueryResults rangePotions = getRangingPotions();
        log.info(RANGING_POTION + " amount: " + rangePotions.size());

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

    private boolean validateRockCake() {
        if (getDwarvenRockCake().isEmpty()) {
            throw new IllegalStateException("Player does not have a rock cake!");
        }

        if (getDwarvenRockCake().get(0).getIndex() < 24) {
            throw new IllegalStateException("Dwarven rock cake must in the last row in inventory");
        }

        return true;
    }

    private Optional<Varbit> getAbsorptionPoints() {
        return ofNullable(load(NamedVarbit.NMZ_ABSORPTION.getId()));
    }

    private boolean hasOverloadPotionEnded() {
        return getOverloadTime().get().getValue() == 0;
    }

    private Optional<Varbit> getOverloadTime() {
        int overloadVarBit = 3955;
        return ofNullable(load(overloadVarBit));
    }

    private void drinkOverloadDose() {
        if (!hasOverloadPotionEnded()) {
            return;
        }
        getOverloadPotions().sortByIndex().get(0).click();
        delayWhile(() -> !getOverloadTime().isPresent() || getOverloadTime().get().getValue() != 20,
                3533, 5345);
        drinkOverloadDose();
    }

    private void drinkAbsorptionPotionsUntilFull() {
        while (!getAbsorptionPoints().isPresent() ||
                (isAbsorptionPointsUnderMax(getAbsorptionPoints().get()) && !getAbsorptionPotions()
                        .isEmpty())) {
            drinkAbsorptionPotion();
        }
    }

    private void guzzleRockCakeUntilHpIs(int i) {
        while (Health.getCurrent() != i) {
            guzzleRockCake();
        }
    }

    private void drinkAbsorptionPotion() {
        getAbsorptionPotions().get(0).click();
        delay(Random.nextInt(190, 330));
    }

    private boolean isAbsorptionPointsUnder(Varbit varbit, int i) {
        return varbit.getValue() < i;
    }

    private boolean isPlayerInADream(Player player) {
        return player.getPosition().getHeight() == -240;
    }

    private void guzzleRockCake() throws IllegalStateException {
        getDwarvenRockCake().get(0).interact("Guzzle");
        delay(Random.nextInt(190, 330) / 3);
    }

    private SpriteItemQueryResults getDwarvenRockCake() {
        return Inventory.getItems(DWARVEN_ROCK_CAKE);
    }

    private boolean isHpGreaterThan(int i) {
        return Health.getCurrent() >= i;
    }

    private boolean isAbsorptionPointsUnderMax(Varbit absorptionPoints) {
        return isAbsorptionPointsUnder(absorptionPoints, 951);
    }

    private SpriteItemQueryResults getAbsorptionPotions() {
        return getPotions(ABSORPTION);
    }

    private SpriteItemQueryResults getOverloadPotions() {
        return getPotions(OVERLOAD);
    }

    private SpriteItemQueryResults getSuperRestorePotions() {
        return getPotions(SUPER_RESTORE);
    }

    private SpriteItemQueryResults getRangingPotions() {
        return getPotions(RANGING_POTION);
    }

    private SpriteItemQueryResults getPotions(String potions) {
        return getItems(potionArray(potions)).sortByIndex();
    }

    public String[] potionArray(String potionName) {
        return IntStream.rangeClosed(1, 4)
                .mapToObj(i -> potionName + "(" + i + ")")
                .toArray(String[]::new);
    }

}
