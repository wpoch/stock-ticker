package com.wpoch.stockticker;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.wpoch.stockticker.client.stockticker.ApiClient;
import com.wpoch.stockticker.client.stockticker.api.DefaultApi;
import com.wpoch.stockticker.client.stockticker.model.AverageStockPriceResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = {AlphaVantageWireMockInitializer.class})
class GetPriceIT {

	@Autowired
	WireMockServer alphaVantageMockServer;

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	AlphaVantageClientConfiguration.AlphaVantageClientConfigurationProperties alphaVantageClientConfigurationProperties;

	@Autowired
	TestRestTemplate testRestTemplate;

	DefaultApi service;

	@BeforeEach
	public void beforeEach(){
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(this.testRestTemplate.getRootUri());
		service = new DefaultApi(apiClient);
	}

	@Test
	void givenAWorkingBackendWhenRequestingThePricesThenTheProperResponseIsRetrieved() {
		// Stub successful get
		this.alphaVantageMockServer.stubFor(
				get(urlMatching("/query?.*"))
						.willReturn(ok()
								.withBodyFile("successful_response_extra_days.json")
								.withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
						)
		);

		AverageStockPriceResponse response = service.priceGet();
		assertThat(response.getSymbol()).isEqualTo(this.applicationProperties.getStockSymbol());
		assertThat(response.getDaily()).hasSize(this.applicationProperties.getNumberOfDaysToRetrieve());
		// TODO Assert AVG

		// Verify the call
		this.alphaVantageMockServer.verify(getRequestedFor(
				urlEqualTo(format("/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
						this.applicationProperties.getStockSymbol(),
						this.alphaVantageClientConfigurationProperties.getApiKey()))
		));
	}

	// TODO: Add test for Rate Limit
	// TODO: Add test Connection Timeout
	// TODO: Add test Server Error (500)
	// TODO: Add test for Error Message (when the stock symbol is not found)
}
