package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BookHandler implements OrderBookHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookHandler.class);
    private static Hitter hitter;

    private BookMaster bookMaster;
    private Symbol symbol;

    public BookHandler(BookMaster b, Hitter h) {
        bookMaster = b;
        hitter = h;
        symbol = bookMaster.getSymbol();
    }

    //Called every 10 seconds and every time a level of the book changes
    @Override
    public void handleRetailState(RetailState retailState) {
        LOGGER.info("Retail State for " + symbol + ": " + retailState.toString());
        bookMaster.processLevels(retailState.getBids(), retailState.getAsks());
        hitter.checkArbitrage();
    }

    //Called every time you make an order, update an order, cancel an order, or your order gets traded
    @Override
    public void handleExposures(ExposureUpdate exposures) {
        LOGGER.info("Exposures " + symbol + ": " + exposures.toString());
    }

    //Called every time you complete a trade
    @Override
    public void handleOwnTrade(OwnTrade trade) {
        LOGGER.info("Trade " + symbol + ": " + trade.toString());
        LOGGER.info(trade.toString());
        hitter.accountTrade(trade);
    }

    //Called when an error occurs
    @Override
    public void handleError(Error e) {
        LOGGER.info("Error " + symbol + ": " + e.toString());
    }

}
