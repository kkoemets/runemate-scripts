package com.kkoemets.api.common;

public class FailSafeCounter {
    private int i;
    private int max;

    private FailSafeCounter() {
        i = 0;
        max = 10;
    }

    public static FailSafeCounter createCounter() {
        return new FailSafeCounter();
    }

    public int increase() {
        if (i >= max) {
            throw new IllegalStateException("Fail safe activated!");
        }
        return ++i;
    }

}