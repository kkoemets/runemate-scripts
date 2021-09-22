package com.kkoemets.scripts.nightmarezone.scripts.presets;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delayWhile;
import static java.util.Optional.ofNullable;

public abstract class AbstractNightmareZoneScript {
    protected static final String OVERLOAD = "Overload ";
    protected static final String SUPER_RESTORE = "Super restore";
    protected static final String RANGING_POTION = "Ranging potion";
    protected BotLogger log;

    protected AbstractNightmareZoneScript(BotLogger log) {
        this.log = log;

    }

    abstract boolean doAdditionalValidations();

    abstract boolean run();

    public abstract ScriptName getScriptName();

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

    protected void drinkOverloadDose() {
        if (!validate()) {
            return;
        }

        if (getOverloadTime().isPresent() && !hasOverloadPotionEnded()) {
            return;
        }
        getOverloadPotions().sortByIndex().get(0).click();
        delayWhile(() -> !getOverloadTime().isPresent() || getOverloadTime().get().getValue() != 20,
                3533, 5345);
        drinkOverloadDose();
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

    protected SpriteItemQueryResults getOverloadPotions() {
        return getPotions(OVERLOAD);
    }

}