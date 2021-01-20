package com.carta.marketdata.model;

public enum MarketDataSource {
    TEST("TEST"),
    TWELVE_DATA("TWELVEDATA"),
    XPRESSFEED("XPRESSFEED"),
    MANUAL("MANUAL"),
    UNKNOWN("UNKNOWN");

    private final String value;

    MarketDataSource(final String text) {
        this.value = text;
    }

    @Override
    public String toString() {
        return value;
    }
}