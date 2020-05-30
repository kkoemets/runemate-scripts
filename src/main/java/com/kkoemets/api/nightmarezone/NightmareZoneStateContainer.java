package com.kkoemets.api.nightmarezone;

public class NightmareZoneStateContainer {

    private final DreamMode DREAM_MODE;

    public NightmareZoneStateContainer(DreamMode DREAM_MODE) {
        this.DREAM_MODE = DREAM_MODE;
    }

    public DreamMode DREAM_MODE() {
        return DREAM_MODE;
    }

    public enum DreamMode {
        ABSORPTION_AND_OVERLOAD_MODE,
        ABSORPTION_MODE,
        SUPER_RESTORE_AND_RANGING_MODE,
        ABSORPTION_AND_RANGING_MODE
    }
}
