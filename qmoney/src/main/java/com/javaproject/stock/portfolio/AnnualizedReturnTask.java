package com.javaproject.stock.portfolio;

import com.javaproject.stock.dto.AnnualizedReturn;
import com.javaproject.stock.dto.Candle;
import com.javaproject.stock.dto.PortfolioTrade;
import com.javaproject.stock.quotes.StockQuotesService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

import static java.time.temporal.ChronoUnit.DAYS;

public class AnnualizedReturnTask implements Callable<AnnualizedReturn> {

    private PortfolioTrade portfolioTrade;
    private LocalDate endDate;
    private StockQuotesService stockQuotesService;

    public AnnualizedReturnTask(PortfolioTrade portfolioTrade,StockQuotesService stockQuotesService, LocalDate endDate) {
        this.portfolioTrade = portfolioTrade;
        this.endDate = endDate;
        this.stockQuotesService = stockQuotesService;
    }

    private AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, PortfolioTrade trade,
                                                        Double buyPrice, Double sellPrice) {
        double total_num_years = DAYS.between(trade.getPurchaseDate(), endDate) / 365.2422;
        double totalReturns = (sellPrice - buyPrice) / buyPrice;
        double annualized_returns = Math.pow((1.0 + totalReturns), (1.0 / total_num_years)) - 1;
        return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);
    }

    @Override
    public AnnualizedReturn call() throws Exception {
        List<Candle> candles =
                stockQuotesService.getStockQuote(portfolioTrade.getSymbol(),
                        portfolioTrade.getPurchaseDate(), endDate);
        return calculateAnnualizedReturns(endDate, portfolioTrade,
                candles.get(0).getOpen(), candles.get(candles.size() - 1).getClose());

    }

}