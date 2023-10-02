package com.javaproject.stock.portfolio;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaproject.stock.dto.AnnualizedReturn;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.quotes.StockQuotesService;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PortfolioManagerImpl implements PortfolioManager {

    private StockQuotesService stockQuoteService;
    private RestTemplate restTemplate;

    // Caution: Do not delete or modify the constructor, or else your build will break!
    // This is absolutely necessary for backward compatibility
    public PortfolioManagerImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PortfolioManagerImpl(StockQuotesService stockQuoteService) {
        this.stockQuoteService = stockQuoteService;
    }


    public PortfolioManagerImpl() {

    }

    // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
    // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
    // into #calculateAnnualizedReturn function here and ensure it follows the method signature.
    // 2. Logic to read Json file and convert them into Objects will not be required further as our
    // clients will take care of it, going forward.

    // Note:
    // Make sure to exercise the tests inside PortfolioManagerTest using command below:
    // ./gradlew test --tests PortfolioManagerTest


    // Extract the logic to call Tiingo third-party APIs to a separate function.
    // Remember to fill out the buildUri function and use that.

    // Build the Url using given parameters and use this function in your code to cann the API.
    public static String prepareUrl(String symbol, LocalDate to, LocalDate endDate, String token) throws RuntimeException {
        if (to.compareTo(endDate) > 0) {
            throw new RuntimeException("End date is greater than purchased date");
        }
        return String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", symbol, to, endDate, token);
    }

    public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade, Double buyPrice, Double sellPrice) {

        int quantity = trade.getQuantity();
        Double totalSoldValue = quantity * sellPrice;
        Double totalBoughtValue = quantity * buyPrice;

        Double totalReturn = (totalSoldValue - totalBoughtValue) / totalBoughtValue;


        LocalDate start = trade.getPurchaseDate();

        Long days = ChronoUnit.DAYS.between(start, endDate);

        String dayString = days.toString();
        Double totalDays = Double.valueOf(dayString);
        Double totalYear = totalDays / 365.0;
        Double annualReturn = (Math.pow((1 + totalReturn), (1 / totalYear))) - 1;
        return new AnnualizedReturn(trade.getSymbol(), annualReturn, totalReturn);
    }

    static Double getOpeningPriceOnStartDate(List<Candle> candles) {
        Candle startCandleObject = candles.get(0);
        return startCandleObject.getOpen();
    }

    public static Double getClosingPriceOnEndDate(List<Candle> candles) {
        Candle lastCandleObject = candles.get(candles.size() - 1);
        return lastCandleObject.getClose();
    }

    private Comparator<AnnualizedReturn> getComparator() {
        return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    }

    public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
        return stockQuoteService.getStockQuote(symbol, from, to);
    }


    @Override
    public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) throws JsonProcessingException {
        ArrayList<AnnualizedReturn> annualizedReturns = new ArrayList<>();
        for (PortfolioTrade portfolioTrade : portfolioTrades) {
            List<Candle> tiingoCandles = getStockQuote(portfolioTrade.getSymbol(), portfolioTrade.getPurchaseDate(), endDate);
            annualizedReturns.add(calculateAnnualizedReturns(endDate, portfolioTrade, getOpeningPriceOnStartDate(tiingoCandles), getClosingPriceOnEndDate(tiingoCandles)));
        }
        Collections.sort(annualizedReturns, getComparator());
        return annualizedReturns;
    }
}
