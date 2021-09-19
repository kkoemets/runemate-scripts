package com.kkoemets.scripts.nightmarezone.scripts;

import com.kkoemets.api.nightmarezone.threshold.GenericThresholdContainerImpl;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static java.util.Optional.ofNullable;

public abstract class AbstractNightmareZoneScript {
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

        if (!validateThatArrowsExistWhenWieldingBow()) {
            throw new IllegalStateException("You have a bow, but where are your arrows?");
        }


        if (!isPlayerInADream(player.get())) {
            throw new IllegalStateException("Player is not in a dream!");
        }

        return true;
    }

    abstract boolean doAdditionalValidations();

    abstract boolean run();

    public abstract ScriptName getScriptName();

    public boolean execute() {
        return validate() && doAdditionalValidations() && run();
    }

    protected boolean validateThatArrowsExistWhenWieldingBow() {
        if (getLocal().getWornItems().stream()
                .noneMatch(item -> item.getName().contains("bow"))) {
            return true;
        }

        return Equipment.getItems().stream()
                .anyMatch(item -> Stream.of("arrow", "bolt")
                        .anyMatch(ammo -> item.getDefinition().getName().contains(ammo)));
    }

    private boolean isPlayerInADream(Player player) {
        return player.getPosition().getHeight() == -240;
    }

    protected boolean isHpGreaterThan(int i) {
        return Health.getCurrent() >= i;
    }


    protected SpriteItemQueryResults getPotions(String potions) {
        return getItems(potionArray(potions)).sortByIndex();
    }

    protected String[] potionArray(String potionName) {
        return IntStream.rangeClosed(1, 4)
                .mapToObj(i -> potionName + "(" + i + ")")
                .toArray(String[]::new);
    }

}