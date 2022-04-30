package com.wpoch.stockticker.service;

import com.wpoch.stockticker.model.HistoricStockPrice;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Service to retrieve the latest price for a stock symbol.
 */
public interface PriceService {

    /**
     * Calculates the historic price for a stock symbol.
     *
     * @param symbol The Stock Symbol to retrieve the price.
     * @param days How many days to use
     * @return The historic symbol price for the last days.
     */
    HistoricStockPrice getHistoricPrice(@NotBlank String symbol, @Min(0) @Max(100) int days);
}
