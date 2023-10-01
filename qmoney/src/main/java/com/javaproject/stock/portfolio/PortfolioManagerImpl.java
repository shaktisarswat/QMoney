package com.javaproject.stock.portfolio;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaproject.stock.dto.AnnualizedReturn;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.dto.TiingoCandle;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PortfolioManagerImpl implements PortfolioManager {

    private RestTemplate restTemplate;

    // Caution: Do not delete or modify the constructor, or else your build will break!
    // This is absolutely necessary for backward compatibility
    protected PortfolioManagerImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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

    // CHECKSTYLE:OFF

    public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
        String url = prepareUrl(trade, endDate, token);
        RestTemplate restTemplate = new RestTemplate();
        TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);
        return Arrays.asList(tiingoCandles);
    }

    // Extract the logic to call Tiingo third-party APIs to a separate function.
    // Remember to fill out the buildUri function and use that.

    // Build the Url using given parameters and use this function in your code to cann the API.
    public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) throws RuntimeException {
        if (trade.getPurchaseDate().compareTo(endDate) > 0) {
            throw new RuntimeException("End date is greater than purchased date");
        }
        return String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", trade.getSymbol(), trade.getPurchaseDate().toString(), endDate, token);
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
        return null;
    }

    @Override
    public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
        ArrayList<AnnualizedReturn> annualizedReturns = new ArrayList<>();
        for (PortfolioTrade portfolioTrade : portfolioTrades) {
            List<Candle> tiingoCandles = fetchCandles(portfolioTrade, endDate, TOKEN);
            annualizedReturns.add(calculateAnnualizedReturns(endDate, portfolioTrade, getOpeningPriceOnStartDate(tiingoCandles), getClosingPriceOnEndDate(tiingoCandles)));
        }
        Collections.sort(annualizedReturns, getComparator());
        return annualizedReturns;
    }
}
