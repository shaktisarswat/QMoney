package com.javaproject.stock.quotes;

import com.javaproject.stock.exception.StockQuoteServiceException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

class AlphavantageLimitTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private AlphavantageService alphavantageService;

    private String aaplQuotes = "{\"Information\": \"The **demo** API key is for demo purposes only. " + "Please claim your free API key at (https://www.alphavantage.co/support/#api-key) to " + "explore our full API offerings. It takes fewer than 20 seconds, and we are committed to " + "making it free forever.\"}";

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void getStockQuoteSingle() {
        Mockito.doReturn(aaplQuotes).when(restTemplate).getForObject(anyString(), eq(String.class));
        try {
            alphavantageService.getStockQuote("AAPL", LocalDate.parse("2019-01-01"), LocalDate.parse("2019-01-04"));
        } catch (Throwable th) {
            if (!(th instanceof StockQuoteServiceException)) {
                fail("Method throwed runtime exception");
            }
        }
    }
}

