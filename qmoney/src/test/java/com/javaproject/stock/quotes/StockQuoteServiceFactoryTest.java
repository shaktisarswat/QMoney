package com.javaproject.stock.quotes;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StockQuoteServiceFactoryTest {

    @Test
    void getServiceTiingo() {
        assertTrue(StockQuoteServiceFactory.INSTANCE.getService("tiingo", new RestTemplate()) instanceof TiingoService);
    }

    @Test
    void getServiceTiingoUpperCase() {
        assertTrue(StockQuoteServiceFactory.INSTANCE.getService("Tiingo", new RestTemplate()) instanceof TiingoService);
    }

    @Test
    void getServiceAlphavantage() {
        assertTrue(StockQuoteServiceFactory.INSTANCE.getService("alphavantage", new RestTemplate()) instanceof AlphavantageService);
    }

    @Test
    void getServiceDefault() {
        assertTrue(StockQuoteServiceFactory.INSTANCE.getService("", new RestTemplate()) instanceof AlphavantageService);
    }
}
