package com.javaproject.stock.quotes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.dto.TiingoCandle;
import com.javaproject.stock.exception.StockQuoteServiceException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


// Implement getStockQuote method below that was also declared in the interface.
// Note:
// 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
// 2. Run the tests using command below and make sure it passes.
// ./gradlew test --tests TiingoServiceTest
public class TiingoService implements StockQuotesService {

    private RestTemplate restTemplate;

    protected TiingoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    // CHECKSTYLE:OFF

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    // Method to create appropriate url to call the Tiingo API.
    @Override
    public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, StockQuoteServiceException {

        if (from.compareTo(to) >= 0) {
            throw new RuntimeException();
        }

        try {
            String url = buildUri(symbol, from, to);
            String stock = restTemplate.getForObject(url, String.class);
            TiingoCandle[] tiingoCandles = getObjectMapper().readValue(stock, TiingoCandle[].class);
            // TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);
            return Arrays.asList(tiingoCandles);
        } catch (NullPointerException e) {
            throw new StockQuoteServiceException("Error occursed when Tiingo API called");
        }

    }

    protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
        String token = "d7ee5290251fd4882f10fde8ada179ccc1450745";
        String uri = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?" + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
        return uri.replace("$APIKEY", token).replace("$SYMBOL", symbol).replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    }

}
