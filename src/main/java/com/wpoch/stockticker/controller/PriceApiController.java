package com.wpoch.stockticker.controller;

import com.wpoch.stockticker.ApplicationProperties;
import com.wpoch.stockticker.api.PriceApi;
import com.wpoch.stockticker.model.AverageStockPriceResponse;
import com.wpoch.stockticker.model.DailyStockPriceResponse;
import com.wpoch.stockticker.model.DatePrice;
import com.wpoch.stockticker.model.HistoricStockPrice;
import com.wpoch.stockticker.service.PriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.ZoneId;

import static java.util.stream.Collectors.toList;

@Controller
public class PriceApiController implements PriceApi {

    private final ApplicationProperties applicationProperties;
    private final PriceService priceService;

    public PriceApiController(
        ApplicationProperties applicationProperties,
        PriceService priceService
    ) {
        this.applicationProperties = applicationProperties;
        this.priceService = priceService;
    }

    @Override
    public ResponseEntity<AverageStockPriceResponse> priceGet() {
        final HistoricStockPrice historicPrice = this.priceService.getHistoricPrice(this.applicationProperties.getStockSymbol(), this.applicationProperties.getNumberOfDaysToRetrieve());
        AverageStockPriceResponse response = AverageStockPriceResponse.builder()
                .symbol(historicPrice.getSymbol())
                .average(BigDecimal.valueOf(historicPrice.getAverage()))
                .daily(
                    historicPrice.getPrices()
                    .stream()
                    .map(this::convertPrices)
                    .collect(toList())
                )
                .build();
        return ResponseEntity.ok(response);
    }

    private DailyStockPriceResponse convertPrices(DatePrice datePrice) {
        return DailyStockPriceResponse.builder()
                .close(BigDecimal.valueOf(datePrice.getPrice()))
                .date(
                        datePrice.getDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                )
                .build();
    }
}
