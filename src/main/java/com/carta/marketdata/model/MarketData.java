package com.carta.marketdata.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MarketData implements Serializable {
    @Serial
    private static final long serialVersionUID = -5835892724045349889L;

    private String symbol;
    private ZonedDateTime dateTimeU;
    private double price;
    private String exchange;
    private SourceType source;
    private double volume;
}
