package com.javaproject.stock;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.*;
import com.javaproject.stock.file.IFilePathLocator;
import com.javaproject.stock.portfolio.PortfolioManagerImpl;
import com.javaproject.stock.quotes.StockQuoteServiceFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class PortfolioManagerApplication {

    private final static String TIINGO_API_TOKEN = "486e149efa731d136eedba8e6ca4fce225c0c650";
//    private final static String TIINGO_API_TOKEN = "39750e91055e7031c232cfa5c952fecbe0c0fbf5";

    public static @NotNull List<String> mainReadFile(@NotNull String[] args, @NotNull String resourceType) throws IOException {
        String path = getPath(args[0], resourceType);
        File file = new File(path);
        ObjectMapper objectMapper = PortfolioManagerApplication.getObjectMapper();

        PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
        ArrayList<String> result = new ArrayList<>();

        for (PortfolioTrade portfolio : portfolioTrades) {
            result.add(portfolio.getSymbol());
        }
        return result;

    }

    @NotNull
    private static String getPath(@NotNull String fileName, @NotNull String resourceType) {
        return IFilePathLocator.ABSOLUTE_SOURCE_PATH + resourceType + fileName;
    }

    private static void printJsonObject(@NotNull Object object) throws IOException {
        Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(object));
    }

    private static @NotNull File resolveFileFromResources(@NotNull String filename) throws URISyntaxException {
        return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();

    }

    private static @NotNull ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public static List<String> mainReadQuotes(String[] args, String resourceType) throws IOException, RuntimeException {

        String endDate = args[1];
        LocalDate date = LocalDate.parse(endDate);

        String path = getPath(args[0], resourceType);
        File file = new File(path);

        ObjectMapper objectMapper = PortfolioManagerApplication.getObjectMapper();
        PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
        RestTemplate restTemple = new RestTemplate();

        List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
        for (PortfolioTrade portfolioTrade : portfolioTrades) {
            String url = prepareUrl(portfolioTrade, date, TIINGO_API_TOKEN);
            TiingoCandle[] tiingoCandles = restTemple.getForObject(url, TiingoCandle[].class);
            assert tiingoCandles != null;
            for (TiingoCandle tiingoCandle : tiingoCandles) {
                TotalReturnsDto totalReturn = new TotalReturnsDto(portfolioTrade.getSymbol(), tiingoCandle.getClose());
                totalReturnsDtos.add(totalReturn);
            }
        }

        sortListByClosingPrice(totalReturnsDtos);

        return findStockSymbol(totalReturnsDtos);

    }

    private static List<String> findStockSymbol(List<TotalReturnsDto> totalReturnsDtos) {
        List<String> result = new ArrayList<>();
        for (TotalReturnsDto totalrDto : totalReturnsDtos) {
            if (!result.contains(totalrDto.getSymbol())) {
                result.add(totalrDto.getSymbol());
            }

        }
        return result;
    }

    private static void sortListByClosingPrice(List<TotalReturnsDto> totalReturnsDtos) {
        Collections.sort(totalReturnsDtos, (t1, t2) -> {
            if (t1.getClosingPrice() >= t2.getClosingPrice()) return 1;
            return -1;
        });
    }

    public static List<String> getStockSymbol(List<PortfolioTrade> portfolioTrade) {
        List<String> result = new ArrayList<>();
        for (PortfolioTrade portfolioTrade2 : portfolioTrade) {
            result.add(portfolioTrade2.getSymbol());
        }
        return result;
    }

    public static List<PortfolioTrade> readTradesFromJson(String filename, String resourceType) throws IOException, URISyntaxException {
        String path = getPath(filename, resourceType);
        File file = new File(path);
        List<PortfolioTrade> portfolioTrade = Arrays.asList(getObjectMapper().readValue(file, PortfolioTrade[].class));
        return portfolioTrade;
    }

    //  Build the Url using given parameters and use this function in your code to cann the API.
    public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) throws RuntimeException {
        if (trade.getPurchaseDate().compareTo(endDate) > 0) {
            throw new RuntimeException("End date is greater than purchased date");
        }
        return String.format("https://api.tiingo.com/tiingo/daily/%s/prices?startDate=%s&endDate=%s&token=%s", trade.getSymbol(), trade.getPurchaseDate().toString(), endDate.toString(), token);
    }

    static Double getOpeningPriceOnStartDate(List<Candle> candles) {
        Candle startCandleObject = candles.get(0);
        return startCandleObject.getOpen();
    }

    public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate) {
        String url = prepareUrl(trade, endDate, TIINGO_API_TOKEN);
        RestTemplate restTemplate = new RestTemplate();
        TiingoCandle[] tiingoCandles = restTemplate.getForObject(url, TiingoCandle[].class);
        return Arrays.asList(tiingoCandles);
    }

    public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args, String resourceType) throws IOException, URISyntaxException {
        LocalDate endDate = LocalDate.parse(args[1]);
        String path = getPath(args[0], resourceType);
        File file = new File(path);
        ObjectMapper objectMapper = PortfolioManagerApplication.getObjectMapper();
        // Extracted all the PortfolioTrades objects for symbol and purchased date
        PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
        ArrayList<AnnualizedReturn> annualizedReturns = new ArrayList<>();

        for (PortfolioTrade portfolioTrade : portfolioTrades) {
            List<Candle> tiingoCandles = fetchCandles(portfolioTrade, endDate);
            annualizedReturns.add(calculateAnnualizedReturns(endDate, portfolioTrade, getOpeningPriceOnStartDate(tiingoCandles), getClosingPriceOnEndDate(tiingoCandles)));
        }

        Collections.sort(annualizedReturns, (a1, a2) -> {
            if (a1.getAnnualizedReturn() >= a2.getAnnualizedReturn()) {
                return -1;
            } else return 1;

        });

        return annualizedReturns;

    }

    // Now that you have the list of PortfolioTrade and their data, calculate annualized returns
    // for the stocks provided in the Json.
    // Use the function you just wrote #calculateAnnualizedReturns.
    // Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.
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


    public static Double getClosingPriceOnEndDate(List<Candle> candles) {
        Candle lastCandleObject = candles.get(candles.size() - 1);
        return lastCandleObject.getClose();
    }

    public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args) throws Exception {
        LocalDate endDate = LocalDate.parse(args[1]);
        File file = resolveFileFromResources(args[0]);
        ObjectMapper objectMapper = PortfolioManagerApplication.getObjectMapper();
        // Extracted all the PortfolioTrades objects for symbol and purchased date
        PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
        return new PortfolioManagerImpl(StockQuoteServiceFactory.INSTANCE.getService("Tiingo", new RestTemplate())).calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
//        return new PortfolioManagerImpl(StockQuoteServiceFactory.INSTANCE.getService("Tiingo", new RestTemplate())).calculateAnnualizedReturnParallel(Arrays.asList(portfolioTrades), endDate,1);
    }


    public static void main(String[] args) throws Exception {
        String fileName = "trades.json";
        String endDate = "2019-12-20";
        printJsonObject(mainReadFile(new String[]{fileName}, IFilePathLocator.MAIN_JAVA_RESOURCE));
        printJsonObject(mainReadQuotes(new String[]{fileName, endDate}, IFilePathLocator.MAIN_JAVA_RESOURCE));
        printJsonObject(mainCalculateSingleReturn(new String[]{fileName, endDate}, IFilePathLocator.MAIN_JAVA_RESOURCE));
        printJsonObject(mainCalculateReturnsAfterRefactor(new String[]{fileName, endDate}));
    }
}

