package com.javaproject.stock.exception;

public class StockQuoteServiceException extends RuntimeException {

    public StockQuoteServiceException(String causeMessage) {
        super(causeMessage);
    }

    public StockQuoteServiceException(String causeMessage, Throwable cause) {
        super(causeMessage, cause);
    }

}
