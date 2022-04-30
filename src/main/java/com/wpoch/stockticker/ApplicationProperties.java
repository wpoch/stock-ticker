package com.wpoch.stockticker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties
public class ApplicationProperties {
    @NotBlank
    String stockSymbol;

    @Max(100)
    @Min(1)
    int numberOfDaysToRetrieve;
}
