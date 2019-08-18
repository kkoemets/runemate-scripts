package com.kkoemets.playersense;

import com.runemate.game.api.hybrid.player_sense.PlayerSense;
import com.runemate.game.api.hybrid.util.calculations.Random;

import java.util.function.Supplier;

import static com.kkoemets.playersense.CustomPlayerSense.Key.*;
import static com.runemate.game.api.hybrid.player_sense.PlayerSense.*;
import static com.runemate.game.api.hybrid.util.calculations.Random.*;

public class CustomPlayerSense {

    public static void initializeKeys() {
        putRandomValueGeneratedByTheKeysValueSupplierIntoPlayerSense();
    }

    private static void putRandomValueGeneratedByTheKeysValueSupplierIntoPlayerSense() {
        for (Key key : values()) {
            if (get(key.name) == null) {
                put(key.name, key.supplier.get());
            }
        }
    }

    public enum Key {
        ACTIVENESS_FACTOR_WHILE_WAITING("kkoemets_activeness_factor", () -> nextDouble(0.2,
                0.8)),
        SPAM_CLICK_COUNT("kkoemets_spam_click_count", () -> nextInt(2, 3)),
        REACTION_TIME("kkoemets_reaction_time", () -> nextLong(200, 320)),
        PERCENTAGE("kkoemets_percentage", () -> nextDouble(0, 1)),
        SHIFT_DROP_REACTION_TIME("kkoemets_shift_drop_reaction_time",
                () -> nextLong(199, 267)),
        AFK_MEDIUM_TIME("kkoemets_afk_medium_time", () -> nextLong(15000, 45000));

        private final String name;
        private final Supplier supplier;

        Key(String name, Supplier supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        public String getKey() {
            return name;
        }

        public Integer getAsInteger() {
            return PlayerSense.getAsInteger(name);
        }

        public Double getAsDouble() {
            return PlayerSense.getAsDouble(name);
        }

        public Long getAsLong() {
            return PlayerSense.getAsLong(name);
        }

        public Boolean getAsBoolean() {
            return PlayerSense.getAsBoolean(name);
        }
    }
}