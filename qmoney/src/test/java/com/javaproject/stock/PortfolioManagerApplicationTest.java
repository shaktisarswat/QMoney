package com.javaproject.stock;

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
        List<String> results = PortfolioManagerApplication
                .mainReadFile(Arrays.toString(new String[]{filename}));

        //then
        Assertions.assertEquals(expected, results);
    }

}
