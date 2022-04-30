package com.wpoch.stockticker;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Initializer for the WireMock server that stubs the REST API calls to the Alpha Vantage service.
 */
public class AlphaVantageWireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration()
                .extensions(new ResponseTemplateTransformer(false))
                .dynamicPort());
        wireMockServer.start();

        // Expose it in the container so we can inject it on the tests
        applicationContext
                .getBeanFactory()
                .registerSingleton("alphaVantageWireMock", wireMockServer);

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        String apiKey = UUID.randomUUID().toString();

        TestPropertyValues
                .of(Map.of(
                "alphavantage.basepath", "http://localhost:" + wireMockServer.port(),
                    "alphavantage.apiKey", apiKey
                ))
                .applyTo(applicationContext);
    }
}
