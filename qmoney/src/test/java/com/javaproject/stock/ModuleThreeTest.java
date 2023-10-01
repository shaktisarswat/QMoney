package com.javaproject.stock;

import com.javaproject.stock.dto.AnnualizedReturn;
import com.javaproject.stock.file.IFilePathLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ModuleThreeTest {

    @Test
    void mainCalculateReturns() throws Exception {
        //given
        String filename = "trades.json";

        //when
        List<AnnualizedReturn> result = PortfolioManagerApplication.mainCalculateSingleReturn(new String[]{filename, "2019-12-12"},IFilePathLocator.TEST_JAVA_RESOURCE);

        //then
        List<String> symbols = result.stream().map(AnnualizedReturn::getSymbol).collect(Collectors.toList());
        Assertions.assertEquals(0.556, result.get(0).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.044, result.get(1).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.025, result.get(2).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"}), symbols);
    }

    @Test
    void mainCalculateReturnsEdgeCase() throws Exception {
        //given
        String filename = "empty.json";

        //when
        List<AnnualizedReturn> result = PortfolioManagerApplication.mainCalculateSingleReturn(new String[]{filename, "2019-12-12"},IFilePathLocator.TEST_JAVA_RESOURCE);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void mainCalculateReturnsVaryingDateRanges() throws Exception {
        //given
        String filename = "trades_invalid_dates.json";
        //when
        List<AnnualizedReturn> result = PortfolioManagerApplication.mainCalculateSingleReturn(new String[]{filename, "2019-12-12"},IFilePathLocator.TEST_JAVA_RESOURCE);

        //then
        List<String> symbols = result.stream().map(AnnualizedReturn::getSymbol).collect(Collectors.toList());
        Assertions.assertEquals(0.36, result.get(0).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.15, result.get(1).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.02, result.get(2).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"}), symbols);

    }


    @Test
    void mainCalculateReturnsInvalidStocks() throws Exception {
        //given
        String filename = "trades_invalid_stock.json";
        //when
        Assertions.assertThrows(RuntimeException.class, () -> PortfolioManagerApplication.mainCalculateSingleReturn(new String[]{filename, "2019-12-12"},IFilePathLocator.TEST_JAVA_RESOURCE));

    }

    @Test
    void mainCalculateReturnsOldTrades() throws Exception {
        //given
        String filename = "trades_old.json";

        //when
        List<AnnualizedReturn> result = PortfolioManagerApplication.mainCalculateSingleReturn(new String[]{filename, "2019-12-20"}, IFilePathLocator.TEST_JAVA_RESOURCE);

        //then
        List<String> symbols = result.stream().map(AnnualizedReturn::getSymbol).collect(Collectors.toList());
        Assertions.assertEquals(0.141, result.get(0).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.091, result.get(1).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(0.056, result.get(2).getAnnualizedReturn(), 0.01);
        Assertions.assertEquals(Arrays.asList(new String[]{"ABBV", "CTS", "MMM"}), symbols);
    }

}
