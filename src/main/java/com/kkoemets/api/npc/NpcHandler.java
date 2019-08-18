package com.kkoemets.api.npc;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.script.framework.logger.BotLogger;

import static com.runemate.game.api.hybrid.region.Npcs.newQuery;

public class NpcHandler {
    private BotLogger log;

    public NpcHandler(BotLogger log) {
        this.log = log;
    }

    public LocatableEntityQueryResults<Npc> getNpcsSortedByDistance(String npc) {
        return newQuery().names(npc).results().sortByDistance();
    }
}