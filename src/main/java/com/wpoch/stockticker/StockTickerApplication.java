package com.wpoch.stockticker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
        // Ignore the default generated components, those are created in @Configuration
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.wpoch\\.stockticker\\.client\\..*")
})
public class StockTickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockTickerApplication.class, args);
    }

}
