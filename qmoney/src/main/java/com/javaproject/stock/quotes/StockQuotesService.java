package com.javaproject.stock.quotes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.exception.StockQuoteServiceException;

import java.time.LocalDate;
import java.util.List;

public interface StockQuotesService {


    List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, StockQuoteServiceException;

}
