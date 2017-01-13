package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderBookHandler;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

public class ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookHandler.class);

    private static final String BOOK_TACO = "TACO";
    private static final String BOOK_BEEF = "BEEF";
    private static final String BOOK_TORT = "TORT";

    private static ExchangeView exchangeView;

    private static final long rateLimit = 10;
    private static long lastTradeTime = 0;

    public ExecutionService(ExchangeView e){
        exchangeView = e;
    }

    /*Execution used for core arbitrage strategy*/
    public void executeBuyTaco(Double tacoAskPrice, Double beefBidPrice, Double tortBidPrice, int volume){
        long time = System.currentTimeMillis();
        if(validArbitrageTime(time)) {
            LOGGER.info("BUYING TACO: " + volume + "@" + tacoAskPrice);
            exchangeView.createOrder(Symbol.of(BOOK_TACO), tacoAskPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
            hedgeTacoExecution(beefBidPrice, tortBidPrice, volume, Side.SELL);
            lastTradeTime = time;
        }
    }

    public void executeSellTaco(Double tacoBidPrice, Double beefAskPrice, Double tortAskPrice, int volume){
        long time = System.currentTimeMillis();
        if(validArbitrageTime(time)) {
            LOGGER.info("SELLING TACO: " + volume + "@" + tacoBidPrice);
            exchangeView.createOrder(Symbol.of(BOOK_TACO), tacoBidPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
            hedgeTacoExecution(beefAskPrice, tortAskPrice, volume, Side.BUY);
            lastTradeTime = time;
        }
    }

    /*Execution used for flattening beef and tort positions*/
    public void executeFlatten(Symbol symbol, Double beefBidPrice, int volume, Side side){
        long time = System.currentTimeMillis();
        if(validFlattenTime(time)) {
            LOGGER.info("ATTEMPTING FLATTEN. " + side.toString().toUpperCase() + " " + symbol + " " + volume + "@" + beefBidPrice);
            exchangeView.createOrder(symbol, beefBidPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
            lastTradeTime = time;
        }
    }

    public void hedgeTacoExecution(Double beefPrice, Double tortPrice, int volume, Side side){
        LOGGER.info("HEDGING TRADE: " + side.toString().toUpperCase() + " BEEF " + volume + "@" + beefPrice);
        exchangeView.createOrder(Symbol.of(BOOK_BEEF), beefPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
        LOGGER.info("HEDGING TRADE: " + side.toString().toUpperCase() + " TORT " + volume + "@" + tortPrice);
        exchangeView.createOrder(Symbol.of(BOOK_TORT), tortPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
    }

    public Boolean validArbitrageTime(long time){
        return ((time - lastTradeTime) >= TimeUnit.SECONDS.toMillis(rateLimit));
    }

    public Boolean validFlattenTime(long time){
        return ((time - lastTradeTime) >= TimeUnit.SECONDS.toMillis(rateLimit/3));
    }

    public void executeQuoteTort(){
        LOGGER.info("Quoting Tort");
        exchangeView.createOrder(Symbol.of(BOOK_TORT), 12d, 3, OrderType.GOOD_TIL_CANCEL, Side.BUY);
        exchangeView.createOrder(Symbol.of(BOOK_TORT), 16d, 3, OrderType.GOOD_TIL_CANCEL, Side.SELL);
    }

    public void executeQuoteBeef(){
        exchangeView.createOrder(Symbol.of(BOOK_TORT), 6d, 3, OrderType.GOOD_TIL_CANCEL, Side.BUY);
        exchangeView.createOrder(Symbol.of(BOOK_TORT), 44d, 3, OrderType.GOOD_TIL_CANCEL, Side.SELL);
    }

    public void cancelOutstanding(){
        exchangeView.massCancel(Symbol.of(BOOK_TACO));
        exchangeView.massCancel(Symbol.of(BOOK_BEEF));
        exchangeView.massCancel(Symbol.of(BOOK_TORT));
    }

}
