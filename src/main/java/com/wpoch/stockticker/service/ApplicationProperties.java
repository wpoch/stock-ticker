package com.wpoch.stockticker.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties
public class ApplicationProperties {
    @NotBlank
    String stockSymbol;

    @Max(100)
    @Min(1)
    int numberOfDaysToRetrieve;
}
