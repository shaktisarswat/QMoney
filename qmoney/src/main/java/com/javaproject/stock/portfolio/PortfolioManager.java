package com.javaproject.stock.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaproject.stock.dto.AnnualizedReturn;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.exception.StockQuoteServiceException;

import java.time.LocalDate;
import java.util.List;

public interface PortfolioManager {

    public static final String TOKEN = "486e149efa731d136eedba8e6ca4fce225c0c650";

    // Note:
    // We will not use file to transfer json data anymore, rather we will try to use java objects.
    // The reason is, this service is going to get exposed as a library in future.

    List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
            throws JsonProcessingException;
    //CHECKSTYLE:ON

    List<AnnualizedReturn> calculateAnnualizedReturnParallel(
            List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException;
}

