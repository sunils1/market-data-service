package com.carta.marketdata.helper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Util {
    public static ZonedDateTime getUTCTime(long timeMs) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeMs), ZoneId.of("UTC"));
    }
}
