package com.wpoch.stockticker.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a stock price over a period of time with the average over that period.
 */
@Value
@Builder
public class HistoricStockPrice {
    double average;
    String symbol;
    List<DatePrice> prices;
}
