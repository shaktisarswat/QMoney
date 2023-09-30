package com.javaproject.stock;

import com.javaproject.stock.file.IFilePathLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ModuleOneTest {

    @Test
    void mainReadFile() throws Exception {
        //given
        String filename = "trades.json";
        List<String> expected = Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"});

        //when
        List<String> results = PortfolioManagerApplication.mainReadFile(new String[]{filename}, IFilePathLocator.TEST_JAVA_RESOURCE);

        //then
        Assertions.assertEquals(expected, results);
    }

    @Test
    void mainReadFileEdgecase() throws Exception {
        //given
        String filename = "empty.json";
        List<String> expected = Arrays.asList(new String[]{});

        //when
        List<String> results = PortfolioManagerApplication.mainReadFile(new String[]{filename}, IFilePathLocator.TEST_JAVA_RESOURCE);

        //then
        Assertions.assertEquals(expected, results);
    }

}
