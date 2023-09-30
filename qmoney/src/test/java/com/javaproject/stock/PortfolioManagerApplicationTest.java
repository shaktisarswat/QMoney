package com.javaproject.stock;

import com.javaproject.stock.file.IFilePathLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class PortfolioManagerApplicationTest {

    @Test
    void mainReadFile() throws Exception {
        //given
        String filename = "trades.json";
        List<String> expected = Arrays.asList(new String[]{"AAPL", "MSFT", "GOOGL"});

        //when
        List<String> results = PortfolioManagerApplication.mainReadFile(new String[]{filename}, IFilePathLocator.MAIN_JAVA_RESOURCE);

        //then
        Assertions.assertEquals(expected, results);
    }


    @Test
    void mainReadQuotes() throws Exception {
        //given
        String filename = "trades.json";
        List<String> expected = Arrays.asList(new String[]{"MSFT", "AAPL", "GOOGL"});

        //when
        List<String> actual = PortfolioManagerApplication.mainReadQuotes(new String[]{filename, "2019-12-12"}, IFilePathLocator.MAIN_JAVA_RESOURCE);

        //then
        Assertions.assertEquals(expected, actual);
    }

}
