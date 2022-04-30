package com.wpoch.stockticker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class StockTickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTickerApplication.class, args);
	}

}
