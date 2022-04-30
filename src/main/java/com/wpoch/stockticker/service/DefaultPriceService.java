package com.wpoch.stockticker.service;

import com.wpoch.stockticker.client.alphavantage.api.DefaultApi;
import com.wpoch.stockticker.client.alphavantage.model.TimeSeriesDailyResponse;
import com.wpoch.stockticker.client.alphavantage.model.TimeSeriesDailyResponseTimeSeriesDaily;
import com.wpoch.stockticker.model.DatePrice;
import com.wpoch.stockticker.model.HistoricStockPrice;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.f;

/**
 * Default implementation for the {@link PriceService}.
 * Uses the external service from AlphaVantage to retrieve the price.
 */
@Slf4j
@Service
public class DefaultPriceService implements PriceService {
    public static final String API_FUNCTION = "TIME_SERIES_DAILY";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final DefaultApi alphaVantageApi;

    public DefaultPriceService(
        DefaultApi alphaVantageApi
    ) {
        this.alphaVantageApi = alphaVantageApi;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable
    public HistoricStockPrice getHistoricPrice(String symbol, int days){
        final TimeSeriesDailyResponse timeSeriesDailyResponse = this.alphaVantageApi.queryGet(API_FUNCTION, symbol);
        // Calculate the amount of days
        if (StringUtils.isNotBlank(timeSeriesDailyResponse.getErrorMessage())) {
            throw new IllegalArgumentException("The downstream API call had an error: " + timeSeriesDailyResponse.getErrorMessage());
        }

        // Assumption the keys are ordered historically
        List<DatePrice> dayPrices = timeSeriesDailyResponse.getTimeSeriesDaily()
                .entrySet()
                .stream()
                .limit(days)
                .map(this::getDatePrice)
                .filter(Objects::nonNull)
                .toList();

        if (dayPrices.isEmpty()) {
            throw new RuntimeException("There was an error and no price was retrieved");
        }

        // Build the response
        double avgClosingPrice = dayPrices.stream().collect(Collectors.averagingDouble(DatePrice::getPrice));
        HistoricStockPrice historicStockPrice = HistoricStockPrice.builder()
                .prices(dayPrices)
                .symbol(symbol)
                .average(avgClosingPrice)
                .build();

        log.debug("Computed historic price", f(historicStockPrice));

        return historicStockPrice;
    }

    /**
     * Build a new {@link DatePrice} from the API response.
     * @param dailyPrice The entry with the day as key and {@link TimeSeriesDailyResponseTimeSeriesDaily} as value.
     * @return The parsed {@link DatePrice} or {@code null} if there is an error parsing.
     */
    private DatePrice getDatePrice(Map.Entry<String, TimeSeriesDailyResponseTimeSeriesDaily> dailyPrice) {
        try {
            return DatePrice.builder()
                    .date(DATE_FORMAT.parse(dailyPrice.getKey()))
                    .price(Double.parseDouble(dailyPrice.getValue().get4close()))
                    .build();
        } catch (Exception ex) {
            log.warn("Couldn't parse the API response date or price.", ex);
            return null;
        }
    }
}
