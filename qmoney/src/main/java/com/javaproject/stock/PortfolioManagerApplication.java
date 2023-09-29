package com.javaproject.stock;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.PortfolioTrade;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class PortfolioManagerApplication {

    public static List<String> mainReadFile(String filename) throws IOException, URISyntaxException {
        File file = new File("C:\\Users\\shakti_sarswat\\Desktop\\JAVA\\QMoney\\qmoney\\src\\main\\resources\\trades.json");
        ObjectMapper objectMapper = PortfolioManagerApplication.getObjectMapper();

        PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
        ArrayList<String> result = new ArrayList<>();

        for (PortfolioTrade portfolio : portfolioTrades) {
            result.add(portfolio.getSymbol());
        }
        return result;
    }

    private static void printJsonObject(Object object) throws IOException {
        Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(object));
    }

    private static File resolveFileFromResources(String filename) throws URISyntaxException {
        return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();

    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
//        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
//        ThreadContext.put("runId", UUID.randomUUID().toString());

        String arg1 = "trade.json";
        printJsonObject(mainReadFile(arg1));

    }
}

