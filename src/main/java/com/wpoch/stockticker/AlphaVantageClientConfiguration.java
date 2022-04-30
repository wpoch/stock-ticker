package com.wpoch.stockticker;

import com.wpoch.stockticker.client.alphavantage.ApiClient;
import com.wpoch.stockticker.client.alphavantage.api.DefaultApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.time.Duration;

import static java.lang.String.format;

/**
 * Configuration for the API Rest client to https://www.alphavantage.co/
 */
@Slf4j
@Configuration
public class AlphaVantageClientConfiguration {

    private final AlphaVantageClientConfigurationProperties properties;

    public AlphaVantageClientConfiguration(@NotNull AlphaVantageClientConfigurationProperties alphaVantageClientConfigurationProperties) {
        this.properties = alphaVantageClientConfigurationProperties;
    }

    @Bean
    ApiClient createAlphaVantageApi() {
        log.info("Initializing the AlphaVantage API Client");
        final RestTemplate restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofMillis(this.properties.getReadTimeoutMs()))
                .setConnectTimeout(Duration.ofMillis(this.properties.getConnectionTimeoutMs()))
                // TODO: Retries
                .build();

        final ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setApiKey(this.properties.getApiKey());
        apiClient.setUserAgent(format("%s/%s", this.getClass().getPackage().getName(), this.getClass().getPackage().getImplementationVersion()));
        apiClient.setBasePath(this.properties.getBasePath());
        return apiClient;
    }

    @Bean
    DefaultApi createAlphaVantageDefaultApi(final @NotNull ApiClient apiClient){
        return new DefaultApi(apiClient);
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(this.properties.getConnectionTimeoutMs());
        clientHttpRequestFactory.setReadTimeout(this.properties.getReadTimeoutMs());
        return clientHttpRequestFactory;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Configuration
    @ConfigurationProperties(prefix = "alphavantage")
    public static class AlphaVantageClientConfigurationProperties {
        @NotBlank
        String basePath;
        @NotBlank
        String apiKey;
        int readTimeoutMs;
        int connectionTimeoutMs;
    }
}
