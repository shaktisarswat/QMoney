package com.javaproject.stock.quotes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.AlphavantageCandle;
import com.javaproject.stock.dto.AlphavantageDailyResponse;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.exception.StockQuoteServiceException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


// Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
// to fetch daily adjusted data for last 20 years.
// Refer to documentation here: https://www.alphavantage.co/documentation/
// --
// The implementation of this functions will be doing following tasks:
// 1. Build the appropriate url to communicate with third-party.
// The url should consider startDate and endDate if it is supported by the provider.
// 2. Perform third-party communication with the url prepared in step#1
// 3. Map the response and convert the same to List<Candle>
// 4. If the provider does not support startDate and endDate, then the implementation
// should also filter the dates based on startDate and endDate. Make sure that
// result contains the records for for startDate and endDate after filtering.
// 5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
// IMP: Do remember to write readable and maintainable code, There will be few functions like
// Checking if given date falls within provided date range, etc.
// Make sure that you write Unit tests for all such functions.
// Note:
// 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
// 2. Run the tests using command below and make sure it passes:
// ./gradlew test --tests AlphavantageServiceTest
// CHECKSTYLE:OFF
// CHECKSTYLE:ON

// 1. Write a method to create appropriate url to call Alphavantage service. The method should
// be using configurations provided in the {@link @application.properties}.
// 2. Use this method in #getStockQuote.

public class AlphavantageService implements StockQuotesService {
    private static final String TOKEN = "SI3HSDSB78QKF78J";
    private static final String FUNCTION = "TIME_SERIES_DAILY";

    private RestTemplate restTemplate;

    public AlphavantageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Override
    public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
            throws JsonProcessingException, StockQuoteServiceException {
        try {
            String url = buildUri(symbol);
            String stock = restTemplate.getForObject(url, String.class);
            System.out.println(stock);
            AlphavantageDailyResponse alphavantageDailyResponse =
                    getObjectMapper().readValue(stock, AlphavantageDailyResponse.class);
            Map<LocalDate, AlphavantageCandle> dailyResponse = alphavantageDailyResponse.getCandles();
            List<Candle> result = new ArrayList<>();

            for (Map.Entry<LocalDate, AlphavantageCandle> entry : dailyResponse.entrySet()) {

                AlphavantageCandle alphavantageCandle = entry.getValue();
                alphavantageCandle.setDate(entry.getKey());
                if (entry.getValue().getDate().isBefore(from) || entry.getValue().getDate().isAfter(to)) {
                    continue;
                }

                result.add(entry.getValue());
            }

            Collections.sort(result, (c1, c2) -> compare(c1, c2));

            return result;

        } catch (NullPointerException e) {
            throw new StockQuoteServiceException("Alphavantage return invalid response");
        }
    }

    private int compare(Candle c1, Candle c2) {
        int compareValue = c1.getDate().compareTo(c2.getDate());
        return compareValue;
    }

    protected String buildUri(String symbol) {

        String url = String.format(
                "https://www.alphavantage.co/query?function=%s&symbol=%s&output=full&apikey=%s", FUNCTION,
                symbol, TOKEN);
        return url;
    }
}

