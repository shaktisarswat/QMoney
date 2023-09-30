package com.javaproject.stock;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.dto.TiingoCandle;
import com.javaproject.stock.dto.TotalReturnsDto;
import com.javaproject.stock.file.IFilePathLocator;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


public class PortfolioManagerApplication {

    private final static String TIINGO_API_TOKEN = "486e149efa731d136eedba8e6ca4fce225c0c650";

    public static @NotNull List<String> mainReadFile(@NotNull String[] args, @NotNull String resourceType) throws IOException {
        String path = getPath(args, resourceType);
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
    private static String getPath(@NotNull String[] args, @NotNull String resourceType) {
        return IFilePathLocator.ABSOLUTE_SOURCE_PATH + resourceType + args[0];
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

        String path = getPath(args, resourceType);
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

    public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
        File file = resolveFileFromResources(filename);
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


    public static void main(String[] args) throws IOException {
        String fileName = "trades.json";
        printJsonObject(mainReadFile(new String[]{fileName}, IFilePathLocator.MAIN_JAVA_RESOURCE));
        printJsonObject(mainReadQuotes(new String[]{fileName}, IFilePathLocator.MAIN_JAVA_RESOURCE));
    }
}

