package com.javaproject.stock;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.file.IFilePathLocator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class PortfolioManagerApplication {
    public static List<String> mainReadFile(String args[], String resourceType) throws IOException, URISyntaxException {
        String path = IFilePathLocator.ABSOLUTE_SOURCE_PATH + resourceType + args[0];
        File file = new File(path);
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
        String fileName = "trades.json";
        printJsonObject(mainReadFile(new String[]{fileName}, IFilePathLocator.MAIN_JAVA_RESOURCE));

    }
}

