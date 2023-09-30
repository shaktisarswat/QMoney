package com.javaproject.stock.dto;

import java.time.LocalDate;

public class PortfolioTrade {

    private String symbol;
    private int quantity;
    private TradeType tradeType;
    private LocalDate purchaseDate;
    public PortfolioTrade() {
    }
    public PortfolioTrade(String symbol, int quantity, LocalDate purchaseDate) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.tradeType = TradeType.BUY;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public static enum TradeType {
        BUY, SELL
    }
}
