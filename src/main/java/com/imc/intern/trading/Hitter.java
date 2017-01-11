package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.views.ExchangeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


public class Hitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hitter.class);
    private final ExchangeView exchangeView;
    // NAJ: would make these values not final and parameterized per book, and possibly a hitter per book.
    /*private static final double bookValue = 20;
    private static final double fixedOffset = .10;
    private static double variableOffset = 0;*/

    private BookMaster tacoMaster;
    private BookMaster beefMaster;
    private BookMaster tortMaster;

    public Hitter(ExchangeView e, BookMaster tacoM, BookMaster beefM, BookMaster tortM){
        exchangeView = e;
        tacoMaster = tacoM;
        beefMaster = beefM;
        tortMaster = tortM;
    }

    public void tacoBuyStrategy(){
        Iterator askLevelsTaco = tacoMaster.getAskLevels().entrySet().iterator();
        Iterator bidLevelsBeef = beefMaster.getBidLevels().entrySet().iterator();
        Iterator bidLevelsTort = tortMaster.getBidLevels().entrySet().iterator();

        //i know this is dumb to break after 1. gonna fix this. had a concurrent modification exception.
        while (askLevelsTaco.hasNext() && bidLevelsBeef.hasNext() && bidLevelsTort.hasNext()) {
            Map.Entry<Double, Integer> tacoAsk = (Map.Entry<Double, Integer>) askLevelsTaco.next();
            Map.Entry<Double, Integer> beefBid = (Map.Entry<Double, Integer>) bidLevelsBeef.next();
            Map.Entry<Double, Integer> tortBid = (Map.Entry<Double, Integer>) bidLevelsTort.next();

            int volume = minOfThree(tacoAsk.getValue(), beefBid.getValue(), tortBid.getValue());

            if (tacoAsk.getKey() < (beefBid.getKey() + tortBid.getKey())) {
                executeBuyTaco(tacoAsk.getKey(), beefBid.getKey(), tortBid.getKey(), volume);
                break;
            } else {
                break;
            }
        }
    }

    public void tacoSellStrategy(){
        Iterator bidLevelsTaco = tacoMaster.getBidLevels().entrySet().iterator();
        Iterator askLevelsBeef = beefMaster.getAskLevels().entrySet().iterator();
        Iterator askLevelsTort = tortMaster.getAskLevels().entrySet().iterator();

        //i know this is dumb to break after 1. gonna fix this. had a concurrent modification exception.
        while(bidLevelsTaco.hasNext() && askLevelsBeef.hasNext() && askLevelsTort.hasNext()){
            Map.Entry<Double,Integer> tacoBid = (Map.Entry<Double,Integer>)bidLevelsTaco.next();
            Map.Entry<Double,Integer> beefAsk = (Map.Entry<Double,Integer>)askLevelsBeef.next();
            Map.Entry<Double,Integer> tortAsk = (Map.Entry<Double,Integer>)askLevelsTort.next();

            int volume = minOfThree(tacoBid.getValue(), beefAsk.getValue(), tortAsk.getValue());

            if(tacoBid.getKey() > (beefAsk.getKey() + tortAsk.getKey())){
                executeSellTaco(tacoBid.getKey(), beefAsk.getKey(), tortAsk.getKey(), volume);
                break;
            }
            else{
                break;
            }
        }
    }

    public void executeBuyTaco(Double tacoAskPrice, Double beefBidPrice, Double tortBidPrice, int volume){
        LOGGER.info("BUYING TACO");
        exchangeView.createOrder(tacoMaster.getSymbol(), tacoAskPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
        tacoMaster.accountOrder(tacoAskPrice, volume, Side.BUY);

        hedgeTacoExecution(beefBidPrice, tortBidPrice, volume, Side.SELL);
    }

    public void executeSellTaco(Double tacoBidPrice, Double beefAskPrice, Double tortAskPrice, int volume){
        LOGGER.info("SELLING TACO");
        exchangeView.createOrder(tacoMaster.getSymbol(), tacoBidPrice, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
        tacoMaster.accountOrder(tacoBidPrice, volume, Side.SELL);

        hedgeTacoExecution(beefAskPrice, tortAskPrice, volume, Side.BUY);
    }

    public void hedgeTacoExecution(Double beefPrice, Double tortPrice, int volume, Side side){
        LOGGER.info("HEDGING EXECUTION");
        //this is where i would execute the order come exercise 3

        beefMaster.accountOrder(beefPrice, volume, side);
        tortMaster.accountOrder(tortPrice, volume, side);
    }

    public int minOfThree(int a, int b, int c){
        return Math.min(a,Math.min(b,c));
    }
}
