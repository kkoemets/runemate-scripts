package com.kkoemets.scripts.blastfurnace;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.GameObjectQueryBuilder;

public class Clicking {

    private Clicking() {
    }

    public static boolean click(GameObjectQueryBuilder gameObject) {
        for (GameObject go : gameObject.results())
            if (go.click())
                return true;
        return false;
    }
}