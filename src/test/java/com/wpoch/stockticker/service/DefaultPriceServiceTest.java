package com.wpoch.stockticker.service;

import com.wpoch.stockticker.client.alphavantage.api.DefaultApi;
import com.wpoch.stockticker.client.alphavantage.model.TimeSeriesDailyResponse;
import com.wpoch.stockticker.client.alphavantage.model.TimeSeriesDailyResponseTimeSeriesDaily;
import com.wpoch.stockticker.model.HistoricStockPrice;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultPriceServiceTest {

    @Mock
    DefaultApi alphaApi;

    DefaultPriceService service;

    @BeforeEach
    public void beforeEach() {
        this.service = new DefaultPriceService(alphaApi);
    }

    @Test
    public void givenMorePricesWhenRequestingThePriceThenReturnTheLastestNth() {
        int days = 8;
        TimeSeriesDailyResponse apiResponse = TimeSeriesDailyResponse.builder()
                .timeSeriesDaily(mockDays(days + 1))
                .build();
        when(this.alphaApi.queryGet(any(), any())).thenReturn(apiResponse);

        HistoricStockPrice result = this.service.getHistoricPrice("any", days);
        assertThat(result.getPrices()).hasSize(days);
    }

    // TODO: Add more tests when there are less values
    // TODO: Add more tests when there are not values
    // TODO: Add more tests when there is an error message in the response

    private Map<String, TimeSeriesDailyResponseTimeSeriesDaily> mockDays(int amountOfDays) {
        Instant now = Instant.now();
        return IntStream.range(1, amountOfDays).boxed()
                .map(i -> Pair.of(getDay(now, i), getDailyValues(i)))
                .collect(Collectors.toMap(x -> x.getLeft(), x -> x.getValue()));
    }

    private String getDay(Instant now, Integer i) {
        return now.minus(i, DAYS).atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }

    private TimeSeriesDailyResponseTimeSeriesDaily getDailyValues(Integer i) {
        return TimeSeriesDailyResponseTimeSeriesDaily.builder()._4close(String.valueOf(i)).build();
    }
}
