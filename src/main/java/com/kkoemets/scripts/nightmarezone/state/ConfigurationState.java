package com.kkoemets.scripts.nightmarezone.state;

public interface ConfigurationState {

    void turnOff();

    void turnOn();

    boolean isRunning();

    void invertToggle();

}