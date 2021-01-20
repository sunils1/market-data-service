package com.carta.marketdata.collector;

import com.carta.marketdata.model.MarketData;
import com.carta.marketdata.model.MarketDataSource;
import com.carta.marketdata.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Component
public class DummyDataCollector extends MarketDataCollector {
    private static final Set<String> SYMBOLS = new HashSet<>(Arrays.asList(
            "A", "AA", "AAPL", "ABC", "ABT", "ACE", "ACN", "ADBE", "ADI", "ADM", "ADP", "ADSK", "ADT", "AEE", "AEP", "AES", "AET",
            "AFL", "AGN", "AIG", "AIV", "AIZ", "AKAM", "ALL", "ALTR", "ALXN", "AMAT", "AMD", "AMGN", "AMP", "AMT", "AMZN", "AN",
            "ANF", "AON", "APA", "APC", "APD", "APH", "APOL", "ARG", "ATI", "AVB", "AVP", "AVY", "AXP", "AZO", "BA", "BAC", "BAX",
            "BBBY", "BBT", "BBY", "BCR", "BDX", "BEAM", "BEN", "BF.B", "BHI", "BIG", "BIIB", "BK", "BLK", "BLL", "BMC", "BMS", "BMY",
            "BRCM", "BRK.B", "BSX", "BTU", "BWA", "BXP", "C", "CA", "CAG", "CAH", "CAM", "CAT", "CB", "CBG", "CBS", "CCE", "CCI", "CCL",
            "CELG", "CERN", "CF", "CFN", "CHK", "CHRW", "CI", "CINF", "CL", "CLF", "CLX", "CMA", "CMCSA", "CME", "CMG", "CMI", "CMS",
            "CNP", "CNX", "COF", "COG", "COH", "COL", "COP", "COST", "COV", "CPB", "CRM", "CSC", "CSCO", "CSX", "CTAS", "CTL", "CTSH",
            "CTXS", "CVC", "CVH", "CVS", "CVX", "D", "DD", "DE", "DELL", "DF", "DFS", "DG", "DGX", "DHI", "DHR", "DIS", "DISCA", "DLTR",
            "DNB", "DNR", "DO", "DOV", "DOW", "DPS", "DRI", "DTE", "DTV", "DUK", "DVA", "DVN", "EA", "EBAY", "ECL", "ED", "EFX", "EIX",
            "EL", "EMC", "EMN", "EMR", "EOG", "EQR", "EQT", "ESRX", "ESV", "ETFC", "ETN", "ETR", "EW", "EXC", "EXPD", "EXPE", "F",
            "FAST", "FCX", "FDO", "FDX", "FE", "FFIV", "FHN", "FII", "FIS", "FISV", "FITB", "FLIR", "FLR", "FLS", "FMC", "FOSL",
            "FRX", "FSLR", "FTI", "FTR", "GAS", "GCI", "GD", "GE", "GILD", "GIS", "GLW", "GME", "GNW", "GOOG", "GPC", "GPS", "GS",
            "GT", "GWW", "HAL", "HAR", "HAS", "HBAN", "HCBK", "HCN", "HCP", "HD", "HES", "HIG", "HNZ", "HOG", "HON", "HOT", "HP",
            "HPQ", "HRB", "HRL", "HRS", "HSP", "HST", "HSY", "HUM", "IBM", "ICE", "IFF", "IGT", "INTC", "INTU", "IP", "IPG", "IR",
            "IRM", "ISRG", "ITW", "IVZ", "JBL", "JCI", "JCP", "JDSU", "JEC", "JNJ", "JNPR", "JOY", "JPM", "JWN", "K", "KEY", "KIM",
            "KLAC", "KMB", "KMI", "KMX", "KO", "KR", "KRFT", "KSS", "L", "LEG", "LEN", "LH", "LIFE", "LLL", "LLTC", "LLY", "LM", "LMT",
            "LNC", "LO", "LOW", "LRCX", "LSI", "LTD", "LUK", "LUV", "LYB", "M", "MA", "MAR", "MAS", "MAT", "MCD", "MCHP", "MCK", "MCO",
            "MDLZ", "MDT", "MET", "MHP", "MJN", "MKC", "MMC", "MMM", "MNST", "MO", "MOLX", "MON", "MOS", "MPC", "MRK", "MRO", "MS",
            "MSFT", "MSI", "MTB", "MU", "MUR", "MWV", "MYL", "NBL", "NBR", "NDAQ", "NE", "NEE", "NEM", "NFLX", "NFX", "NI", "NKE",
            "NOC", "NOV", "NRG", "NSC", "NTAP", "NTRS", "NU", "NUE", "NVDA", "NWL", "NWSA", "NYX", "OI", "OKE", "OMC", "ORCL", "ORLY",
            "OXY", "PAYX", "PBCT", "PBI", "PCAR", "PCG", "PCL", "PCLN", "PCP", "PCS", "PDCO", "PEG", "PEP", "PETM", "PFE", "PFG", "PG",
            "PGR", "PH", "PHM", "PKI", "PLD", "PLL", "PM", "PNC", "PNR", "PNW", "POM", "PPG", "PPL", "PRGO", "PRU", "PSA", "PSX", "PWR",
            "PX", "PXD", "QCOM", "QEP", "R", "RAI", "RDC", "RF", "RHI", "RHT", "RL", "ROK", "ROP", "ROST", "RRC", "RRD", "RSG", "RTN",
            "S", "SAI", "SBUX", "SCG", "SCHW", "SE", "SEE", "SHW", "SIAL", "SJM", "SLB", "SLM", "SNA", "SNDK", "SNI", "SO", "SPG",
            "SPLS", "SRCL", "SRE", "STI", "STJ", "STT", "STX", "STZ", "SWK", "SWN", "SWY", "SYK", "SYMC", "SYY", "T", "TAP", "TDC",
            "TE", "TEG", "TEL", "TER", "TGT", "THC", "TIE", "TIF", "TJX", "TMK", "TMO", "TRIP", "TROW", "TRV", "TSN", "TSO", "TSS",
            "TWC", "TWX", "TXN", "TXT", "TYC", "UNH", "UNM", "UNP", "UPS", "URBN", "USB", "UTX", "V", "VAR", "VFC", "VIAB", "VLO",
            "VMC", "VNO", "VRSN", "VTR", "VZ", "WAG", "WAT", "WDC", "WEC", "WFC", "WFM", "WHR", "WIN", "WLP", "WM", "WMB", "WMT", "WPI",
            "WPO", "WPX", "WU", "WY", "WYN", "WYNN", "X", "XEL", "XL", "XLNX", "XOM", "XRAY", "XRX", "XYL", "YHOO", "YUM", "ZION", "ZMH"
    ));

    private String getExchange() {
        return "NASDAQ";
    }

    private BigDecimal getVolume() {
        return BigDecimal.valueOf(Math.round(Math.random() * 1000));
    }

    private ZonedDateTime getTime() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    private double getPrice(String sym) {
        return Math.random() * (sym.hashCode() + System.currentTimeMillis() % 1000) / 1000.0;
    }

    @Override
    public Set<String> getSymbols() {
        return SYMBOLS;
    }

    @Override
    public MarketData getData(String symbol) {
        sleep();
        return new MarketData(symbol, getTime(), BigDecimal.valueOf(getPrice(symbol)),
                getExchange(), MarketDataSource.TEST, this.getVolume());
    }

    public DummyDataCollector(Repository repository) {
        super(repository);
    }

    private void sleep() {
        try {
            Thread.sleep(Math.round(Math.random() * 300));
        } catch (InterruptedException e) {
            log.error("Sleep was interrupted {}", e.getMessage());
        }
    }
}
