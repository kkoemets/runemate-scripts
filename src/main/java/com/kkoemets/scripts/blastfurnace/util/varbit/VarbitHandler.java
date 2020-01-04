package com.kkoemets.scripts.blastfurnace.util.varbit;

import com.runemate.game.api.hybrid.local.Varbit;

import java.util.Map;
import java.util.Set;

import static com.runemate.game.api.hybrid.local.Varbits.loadAll;
import static java.util.stream.Collectors.toMap;

public class VarbitHandler {

    public static Map<Integer, Integer> getVarbits() {
        return loadAll().stream().collect(toMap(Varbit::getId, Varbit::getValue));
    }

    static Map<Integer, Integer> filterChangedVarbits(Map<Integer, Integer> oldVarbits,
                                                      Map<Integer, Integer> newVarbits,
                                                      Set<Integer> ignoredVarbits) {
        return newVarbits.entrySet().stream()
                .filter(entry -> varbitIsNotIgnored(ignoredVarbits, entry) && varbitValueHasChanged(oldVarbits, entry))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean varbitIsNotIgnored(Set<Integer> ignoredVarbits, Map.Entry<Integer, Integer> entry) {
        return !ignoredVarbits.contains(entry.getKey());
    }

    private static boolean varbitValueHasChanged(Map<Integer, Integer> oldVarbits, Map.Entry<Integer, Integer> entry) {
        return !(oldVarbits.containsKey(entry.getKey()) && oldVarbits.get(entry.getKey()).equals(entry.getValue()));
    }

}