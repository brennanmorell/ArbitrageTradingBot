package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderBookHandler;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by imc on 12/01/2017.
 */
public class ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookHandler.class);
    private ExchangeView exchangeView;

    private static final String BOOK_TACO = "TACO";
    private static final String BOOK_BEEF = "BEEF";
    private static final String BOOK_TORT = "TORT";

    private static long rateLimit = 30; // msanders: this limit doesn't change, so why not make it "final"
    private static long lastBuyTime = 0;

    public ExecutionService(ExchangeView e){
        exchangeView = e;
    }

    /*Execution used for core arbitrage strategy*/
    public void executeBuyTaco(Double tacoAskPrice, Double beefBidPrice, Double tortBidPrice, int volume){
        long buyTime = System.currentTimeMillis();
        if(validTime(buyTime)) {
            LOGGER.info("BUYING TACO");
            exchangeView.createOrder(Symbol.of(BOOK_TACO), tacoAskPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
            hedgeTacoExecution(beefBidPrice, tortBidPrice, volume, Side.SELL);
            lastBuyTime = buyTime;
        }
    }

    public void executeSellTaco(Double tacoBidPrice, Double beefAskPrice, Double tortAskPrice, int volume){
        long buyTime = System.currentTimeMillis();
        if(validTime(buyTime)) {
            LOGGER.info("SELLING TACO");
            exchangeView.createOrder(Symbol.of(BOOK_TACO), tacoBidPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
            hedgeTacoExecution(beefAskPrice, tortAskPrice, volume, Side.BUY);
            lastBuyTime = buyTime;
        }
    }

    /*Execution used for flattening beef and tort positions*/
    public void executeFlatten(Symbol symbol, Double beefBidPrice, int volume, Side side){
        LOGGER.info("TRYING TO " + side.toString().toUpperCase() + " " + volume + " FOR " + symbol);
        long buyTime = System.currentTimeMillis();
        if(validTime(buyTime)) {
            exchangeView.createOrder(symbol, beefBidPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
            lastBuyTime = buyTime;
        }
    }

    public void hedgeTacoExecution(Double beefPrice, Double tortPrice, int volume, Side side){
        LOGGER.info("HEDGING TRADE: " + side.toString().toUpperCase() + " BEEF AND " + side.toString().toUpperCase() + " TORT");
        //this is where i would execute the order come exercise 3
        exchangeView.createOrder(Symbol.of(BOOK_BEEF), beefPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
        exchangeView.createOrder(Symbol.of(BOOK_TORT), tortPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, side);
    }

    public Boolean validTime(long buyTime){
        return buyTime - lastBuyTime >= TimeUnit.SECONDS.toMillis(rateLimit);
    }
}
