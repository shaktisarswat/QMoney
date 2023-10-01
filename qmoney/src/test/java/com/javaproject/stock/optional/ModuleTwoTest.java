package com.javaproject.stock.optional;

import com.javaproject.stock.PortfolioManagerApplication;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.file.IFilePathLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ModuleTwoTest {

    @Test
    void readStockFromJson() throws Exception {
        //given
        String filename = "trades.json";
        List<String> expected = Arrays.asList(new String[]{"MSFT", "CSCO", "CTS"});

        //when
        List<PortfolioTrade> trades = PortfolioManagerApplication.readTradesFromJson(filename, IFilePathLocator.TEST_JAVA_RESOURCE);
        List<String> actual = trades.stream().map(PortfolioTrade::getSymbol).collect(Collectors.toList());

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void prepareUrl() throws Exception {
        //given
        PortfolioTrade trade = new PortfolioTrade();
        trade.setPurchaseDate(LocalDate.parse("2010-01-01"));
        trade.setSymbol("AAPL");
        String token = "abcd";
        //when
        String tiingoUrl = PortfolioManagerApplication.prepareUrl(trade, LocalDate.parse("2010-01-10"), token);

        //then
        String uri = "https://api.tiingo.com/tiingo/daily/AAPL/prices?startDate=2010-01-01&endDate=2010-01-10&token=abcd";

        Assertions.assertEquals(tiingoUrl, uri);
    }


}
