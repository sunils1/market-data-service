package com.carta.marketdata.constants;

public enum MarketDataSource {
    TEST("UNKNOWN"),
    TWELVE_DATA("TWELVEDATA"),
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