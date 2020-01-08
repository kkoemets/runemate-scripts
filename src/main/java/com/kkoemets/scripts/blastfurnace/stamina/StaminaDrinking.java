package com.kkoemets.scripts.blastfurnace.stamina;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;

import static com.kkoemets.scripts.blastfurnace.BlastFurnaceItems.GOLD_ORE;
import static com.kkoemets.scripts.varbitlogger.NamedVarbit.RUN_SLOWED_DEPLETION_ACTIVE;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.deposit;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Bank.isOpen;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.isFull;
import static com.runemate.game.api.script.Execution.delay;

public class StaminaDrinking {

    private static final String[] STAMINA_POTIONS =
            {"Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)"};

    private StaminaDrinking() {
    }

    public static boolean hasStaminaExpired() {
        return load(RUN_SLOWED_DEPLETION_ACTIVE.getId()).getValue() == 0;
    }

    public static boolean takeStaminaFromOpenedBankAndCloseBankAndDrink() {
        if (!hasStaminaExpired()) {
            return true;
        }

        if (isFull() && deposit(GOLD_ORE, 1)) {
            return takeStaminaFromOpenedBankAndCloseBankAndDrink();
        }

        if (!isOpen() && !getItems(STAMINA_POTIONS).isEmpty()
                && getItems(STAMINA_POTIONS).get(0).click() && delay(700, 900)) {
            return takeStaminaFromOpenedBankAndCloseBankAndDrink();
        }

        if (isOpen() && !getItems(STAMINA_POTIONS).isEmpty() && Bank.close()) {
            return takeStaminaFromOpenedBankAndCloseBankAndDrink();
        }

        if (isOpen() && getItems(STAMINA_POTIONS).isEmpty()) {
            Bank.getItems(STAMINA_POTIONS).get(0).click();
        }

        delay(1200, 1600);

        return takeStaminaFromOpenedBankAndCloseBankAndDrink();
    }

}