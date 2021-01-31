package com.carta.marketdata.collector.twelvedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;

@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class MetaData {
    String symbol;
    String interval;
    String currency;
    String exchange_timezone;
    String exchange;
    String type;
}

@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class TimeSeriesValue {
    String datetime;
    String open;
    String high;
    String low;
    String close;
    String volume;
}

@ToString
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class TwelveDataResponse {
    MetaData meta;
    TimeSeriesValue[] values;
}
