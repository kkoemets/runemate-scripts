package com.kkoemets.scripts.nightmarezone.state;

import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;

import javax.annotation.Nullable;

public class NightmareZoneConfigurationState implements ConfigurationState {
    private boolean isRunning = false;
    private AbstractNightmareZoneScript currentScript = null;

    @Override
    public void turnOff() {
        isRunning = false;
    }

    @Override
    public void turnOn() {
        isRunning = true;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void invertToggle() {
        if (isRunning) {
            turnOff();
        } else {
            turnOn();
        }
    }


    @Nullable
    public AbstractNightmareZoneScript getCurrentScript() {
        return currentScript;
    }

    public void setCurrentScript(AbstractNightmareZoneScript currentScript) {
        this.currentScript = currentScript;
    }

}