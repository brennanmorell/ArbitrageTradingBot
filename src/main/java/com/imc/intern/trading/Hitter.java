package com.imc.intern.trading;

import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by imc on 10/01/2017.
 */
public class Hitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hitter.class);

    // NAJ: would make these values not final and parameterized per book, and possibly a hitter per book.
    private static final double bookValue = 20;
    private static final double fixedOffset = .10;
    private static final double variableOffset = 0;

    private final ExchangeView exchangeView;


    public Hitter(ExchangeView e){
        exchangeView = e;
    }

    public void buyStrategy(Map<Double, Integer> askLevels, Symbol book){
        for (Map.Entry<Double, Integer> entry : askLevels.entrySet()) {
            Double price = entry.getKey();
            Integer volume = entry.getValue();
            if (price < (bookValue - fixedOffset - variableOffset)) {
                LOGGER.info("ATTEMPTING TO BUY");
                long orderID = exchangeView.createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
            }
        }
    }

    public void sellStrategy(Map<Double, Integer> bidLevels, Symbol book){
        Iterator it = bidLevels.entrySet().iterator(); // NAJ: Unused code
        for (Map.Entry<Double, Integer> entry : bidLevels.entrySet()) {
            Double price = entry.getKey();
            Integer volume = entry.getValue();
            if(entry.getKey() > (bookValue+fixedOffset-variableOffset)){
                LOGGER.info("ATTEMPTING TO SELL");
                long orderID = exchangeView.createOrder(book, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
            }
        }
    }
}
