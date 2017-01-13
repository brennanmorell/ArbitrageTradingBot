package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OwnTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


public class Hitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hitter.class);

    private static final int maxTradeVolume = 20;
    private static final int maxLevelDepth = 2;

    private BookMaster tacoMaster;
    private BookMaster beefMaster;
    private BookMaster tortMaster;

    private PositionTracker tracker;

    private ExecutionService executionService;


    public Hitter(BookMaster tacoM, BookMaster beefM, BookMaster tortM, ExecutionService e, PositionTracker t){
        tacoMaster = tacoM;
        beefMaster = beefM;
        tortMaster = tortM;
        executionService = e;
        tracker = t;
    }

    public void checkArbitrage(){
        if(validPositions()){
            tacoBuyStrategy();
            tacoSellStrategy();
        }
    }

    public Boolean validPositions(){
        int beefPosition = tracker.getBeefPosition();
        int tortPosition = tracker.getTortPosition();

        int maxLongPosition = tracker.getMaxLongPosition();
        int maxShortPosition = tracker.getMaxShortPosition();

        return (checkShort(tortPosition, maxShortPosition, tortMaster) &&
        checkLong(tortPosition, maxLongPosition, tortMaster) &&
        checkShort(beefPosition, maxShortPosition, beefMaster) &&
        checkLong(beefPosition, maxLongPosition, beefMaster));
    }

    public Boolean checkLong(int position, int maxLongPosition, BookMaster master){
        if(position > maxLongPosition){
            flattenLong(position, master);
            return false;
        }
        else if(position > 0){
            //Would be good time to quote sell for master.symbol()

        }
        return true;
    }

    public Boolean checkShort(int position, int maxShortPosition, BookMaster master){
        if(position < maxShortPosition){
            flattenShort(position, master);
            return false;
        }
        else if(position < 0){
            //Would be good time to quote buy for master.symbol()
        }
        return true;
    }

    public void flattenLong(int position, BookMaster master){
        Iterator bidLevels = master.getBidLevels().entrySet().iterator();
        while(position > 0 && bidLevels.hasNext()){
            Map.Entry<Double, Integer> bid = (Map.Entry<Double, Integer>)bidLevels.next();
            int volume = Math.min(bid.getValue(), Math.abs(position)); //make sure not to over sell and end up short
            volume = Math.min(volume, maxTradeVolume);
            executionService.executeFlatten(master.getSymbol(), bid.getKey(), volume, Side.SELL);
            position-=volume;
        }
    }

    public void flattenShort(int position, BookMaster master){
        Iterator askLevels = master.getAskLevels().entrySet().iterator();
        while(position < 0 && askLevels.hasNext()){
            Map.Entry<Double, Integer> ask = (Map.Entry<Double, Integer>)askLevels.next();
            int volume = Math.min(ask.getValue(), Math.abs(position)); //make sure not to over sell and end up long
            volume = Math.min(volume, maxTradeVolume);
            executionService.executeFlatten(master.getSymbol(), ask.getKey(), volume, Side.BUY);
            position+=volume;
        }
    }

    public void tacoBuyStrategy(){
        Iterator<Map.Entry<Double, Integer>> askLevelsTaco = tacoMaster.getAskLevels().entrySet().iterator();
        Iterator<Map.Entry<Double, Integer>> bidLevelsBeef = beefMaster.getBidLevels().entrySet().iterator();
        Iterator<Map.Entry<Double, Integer>> bidLevelsTort = tortMaster.getBidLevels().entrySet().iterator();

        int levelCount = 0;
        while (askLevelsTaco.hasNext() && bidLevelsBeef.hasNext() && bidLevelsTort.hasNext() && levelCount < maxLevelDepth) {
            Map.Entry<Double, Integer> tacoAsk = askLevelsTaco.next();
            Map.Entry<Double, Integer> beefBid = bidLevelsBeef.next();
            Map.Entry<Double, Integer> tortBid = bidLevelsTort.next();

            int volume = minOfThree(tacoAsk.getValue(), beefBid.getValue(), tortBid.getValue());
            volume = Math.min(volume, maxTradeVolume); //imposed volume limit
            if (tacoAsk.getKey() < (beefBid.getKey() + tortBid.getKey())) {
                executionService.executeBuyTaco(tacoAsk.getKey(), beefBid.getKey(), tortBid.getKey(), volume);
            } else {
                break;
            }
            levelCount++;
        }
    }

    public void tacoSellStrategy(){
        Iterator<Map.Entry<Double, Integer>> bidLevelsTaco = tacoMaster.getBidLevels().entrySet().iterator();
        Iterator<Map.Entry<Double, Integer>> askLevelsBeef = beefMaster.getAskLevels().entrySet().iterator();
        Iterator<Map.Entry<Double, Integer>> askLevelsTort = tortMaster.getAskLevels().entrySet().iterator();

        int levelCount = 0;
        while(bidLevelsTaco.hasNext() && askLevelsBeef.hasNext() && askLevelsTort.hasNext() && levelCount < maxLevelDepth){
            Map.Entry<Double,Integer> tacoBid = bidLevelsTaco.next();
            Map.Entry<Double,Integer> beefAsk = askLevelsBeef.next();
            Map.Entry<Double,Integer> tortAsk = askLevelsTort.next();

            int volume = minOfThree(tacoBid.getValue(), beefAsk.getValue(), tortAsk.getValue());
            volume = Math.min(volume, maxTradeVolume);
            if(tacoBid.getKey() > (beefAsk.getKey() + tortAsk.getKey())){
                executionService.executeSellTaco(tacoBid.getKey(), beefAsk.getKey(), tortAsk.getKey(), volume);
            }
            else{
                break;
            }
            levelCount++;
        }
    }

    public void accountTrade(OwnTrade trade){
        if(trade.getBook().equals(tacoMaster.getSymbol())){
            tacoMaster.updateBooks(trade.getPrice(), trade.getVolume(), trade.getSide());
        }
        else if(trade.getBook().equals(beefMaster.getSymbol())){
            beefMaster.updateBooks(trade.getPrice(), trade.getVolume(), trade.getSide());
        }
        else{
            tortMaster.updateBooks(trade.getPrice(), trade.getVolume(), trade.getSide());
        }

        updatePositions(trade);
    }

    public void updatePositions(OwnTrade trade){
        if(trade.getBook().equals(tacoMaster.getSymbol())){
            tracker.updateBeefPosition(trade.getVolume(), trade.getSide());
            tracker.updateTortPosition(trade.getVolume(), trade.getSide());
        }
        else if(trade.getBook().equals(beefMaster.getSymbol())){
            tracker.updateBeefPosition(trade.getVolume(), trade.getSide());
        }
        else if(trade.getBook().equals(tortMaster.getSymbol())){
            tracker.updateTortPosition(trade.getVolume(), trade.getSide());
        }
    }

    public void quoteTort(){
        executionService.executeQuoteTort();
    }

    public void quoteBeef(){
        executionService.executeQuoteBeef();
    }

    public void withdrawFromMarket(){
        executionService.cancelOutstanding();
    }


    public int minOfThree(int a, int b, int c){
        return Math.min(a,Math.min(b,c));
    }
}
