package com.carta.marketdata.model;

public enum SourceType {
    TEST("TEST"),
    TWELVE_DATA("TWELVEDATA"),
    MANUAL("MANUAL"),
    UNKNOWN("UNKNOWN");

    private final String value;

    SourceType(final String text) {
        this.value = text;
    }

    @Override
    public String toString() {
        return value;
    }
}