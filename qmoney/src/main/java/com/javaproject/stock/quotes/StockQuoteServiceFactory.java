package com.javaproject.stock.quotes;

import org.springframework.web.client.RestTemplate;

public enum StockQuoteServiceFactory {
    INSTANCE;
    // Note: (Recommended reading)
    // Pros and cons of implementing Singleton via enum.
    // https://softwareengineering.stackexchange.com/q/179386/253205

    // Make sure that you have implemented TiingoService and AlphavantageService
    // as per the instructions and the tests are passing for them.
    // Implement the factory function such that it will return TiingoService instance when
    // provider == 'tiingo' (Case insensitive)
    // Return new instance of AlphavantageService in all other cases.
    // RestTemplate is passed as a parameter along, and you will have to pass the same to
    // the constructor of corresponding class.
    // Run the tests using command below and make sure it passes
    // ./gradlew test --tests StockQuoteServiceFactory

    public StockQuotesService getService(String provider, RestTemplate restTemplate) {

        if (provider != null) {
            if (provider.toLowerCase().equals("tiingo")) {
                return new TiingoService(restTemplate);
            } else if (provider.toLowerCase().equals("alphavantage")) {
                return new AlphavantageService(restTemplate);
            }
        }
        return new AlphavantageService(restTemplate);
    }
}
