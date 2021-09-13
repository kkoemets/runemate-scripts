package com.kkoemets.scripts.nightmarezone.scripts;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.kkoemets.scripts.varbitlogger.NamedVarbit;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Optional.ofNullable;

public abstract class AbstractNightmareZoneScript {
    private static final String ABSORPTION = "Absorption ";
    private static final String DWARVEN_ROCK_CAKE = "Dwarven rock cake";
    protected BotLogger log;

    protected ThresholdContainer hpThresholdContainer;
    protected ThresholdContainer absorptionPointsThreshold;
    protected ThresholdContainer prayerThresholdContainer;
    protected ThresholdContainer rangeBuffThresholdContainer;

    protected AbstractNightmareZoneScript(BotLogger log) {
        this.log = log;

        hpThresholdContainer = new GenericThresholdContainerImpl(2, 4);
        absorptionPointsThreshold = new GenericThresholdContainerImpl(769, 821);
        rangeBuffThresholdContainer = new GenericThresholdContainerImpl(5, 8);
        prayerThresholdContainer = new GenericThresholdContainerImpl(9, 26);
    }

    protected boolean validate() {
        Optional<Player> player = ofNullable(getLocal());
        if (!player.isPresent()) {
            log.info("Player is not visible yet, waiting...");
            return false;
        }


        if (!isPlayerInADream(player.get())) {
            throw new IllegalStateException("Player is not in a dream!");
        }

        return true;
    }

    abstract boolean doAdditionalValidations();

    abstract boolean run();

    public boolean execute() {
        return validate() && doAdditionalValidations() && run();
    }

    protected boolean validateRockCake() {
        if (getDwarvenRockCake().isEmpty()) {
            throw new IllegalStateException("Player does not have a rock cake!");
        }

        if (getDwarvenRockCake().get(0).getIndex() < 24) {
            throw new IllegalStateException("Dwarven rock cake must in the last row in inventory");
        }

        return true;
    }

    protected Optional<Varbit> getAbsorptionPoints() {
        return ofNullable(load(NamedVarbit.NMZ_ABSORPTION.getId()));
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

    private void drinkAbsorptionPotion() {
        getAbsorptionPotions().get(0).click();
        delay(Random.nextInt(190, 330));
    }

    protected boolean isAbsorptionPointsUnder(Varbit varbit, int i) {
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

    protected boolean isHpGreaterThan(int i) {
        return Health.getCurrent() >= i;
    }

    private boolean isAbsorptionPointsUnderMax(Varbit absorptionPoints) {
        return isAbsorptionPointsUnder(absorptionPoints, 951);
    }

    protected SpriteItemQueryResults getAbsorptionPotions() {
        return getPotions(ABSORPTION);
    }


    protected SpriteItemQueryResults getPotions(String potions) {
        return getItems(potionArray(potions)).sortByIndex();
    }

    public String[] potionArray(String potionName) {
        return IntStream.rangeClosed(1, 4)
                .mapToObj(i -> potionName + "(" + i + ")")
                .toArray(String[]::new);
    }

}