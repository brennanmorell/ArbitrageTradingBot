package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OwnTrade;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;



public class Hitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hitter.class);

    // NAJ: would make these values not final and parameterized per book, and possibly a hitter per book.
    /*private static final double bookValue = 20;
    private static final double fixedOffset = .10;
    private static double variableOffset = 0;*/

    private static int maxTradeVolume = 100;

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
            LOGGER.info("Valid positions. Proceeding to buy and sell strategies.");
            tacoBuyStrategy();
            tacoSellStrategy();
        }
    }

    public Boolean validPositions(){
        int beefPosition = tracker.getBeefPosition();
        int tortPosition = tracker.getTortPosition();

        LOGGER.info("BEEF POSITION IS " + beefPosition);
        LOGGER.info("TORT POSITION IS " + tortPosition);

        int maxLongPosition = tracker.getMaxLongPosition();
        int maxShortPosition = tracker.getMaxShortPosition();

        if(beefPosition > maxLongPosition){
            LOGGER.info("Beef position beyond max long. Adjusting position.");
            flattenLong(beefPosition, maxLongPosition, beefMaster);
            return false;
        }
        else if(beefPosition < maxShortPosition){
            LOGGER.info("Beef position beyond max short. Adjusting position.");
            flattenShort(beefPosition, maxShortPosition, beefMaster);
            return false;
        }
        else if(tortPosition > maxLongPosition){
            LOGGER.info("Tort position beyond max long. Adjusting position.");
            flattenLong(tortPosition, maxLongPosition, tortMaster);
            return false;
        }
        else if(tortPosition < maxShortPosition){
            LOGGER.info("Tort position beyond max short. Adjusting position.");
            flattenShort(tortPosition, maxShortPosition, tortMaster);
            return false;
        }
        else{
            return true;
        }
    }

    public void flattenLong(int position, int maxLongPosition, BookMaster master){
        LOGGER.info("Attempting to flatten long beef position.");
        Iterator bidLevels = master.getBidLevels().entrySet().iterator();
        while(position - maxLongPosition > 0 && bidLevels.hasNext()){
            Map.Entry<Double, Integer> bid = (Map.Entry<Double, Integer>)bidLevels.next();
            int volume = Math.min(bid.getValue(), Math.abs(position)); //makes sure not to over sell and end up short
            volume = Math.min(volume, maxTradeVolume);
            executionService.executeFlatten(master.getSymbol(), bid.getKey(), volume, Side.SELL);
            position-=volume;
        }
    }

    public void flattenShort(int position, int maxShortPosition, BookMaster master){
        LOGGER.info("Attempting to flatten short beef position.");
        Iterator askLevels = master.getAskLevels().entrySet().iterator();
        while(maxShortPosition - position > 0 && askLevels.hasNext()){
            Map.Entry<Double, Integer> ask = (Map.Entry<Double, Integer>)askLevels.next();
            int volume = Math.min(ask.getValue(), Math.abs(position)); //makes sure not to over sell and end up long
            volume = Math.min(volume, maxTradeVolume);
            executionService.executeFlatten(master.getSymbol(), ask.getKey(), volume, Side.BUY);
            position+=volume;
        }
    }

    public void tacoBuyStrategy(){
        Iterator askLevelsTaco = tacoMaster.getAskLevels().entrySet().iterator();
        Iterator bidLevelsBeef = beefMaster.getBidLevels().entrySet().iterator();
        Iterator bidLevelsTort = tortMaster.getBidLevels().entrySet().iterator();

        int levelCount = 0;

        while (askLevelsTaco.hasNext() && bidLevelsBeef.hasNext() && bidLevelsTort.hasNext() && levelCount < 5) {
            Map.Entry<Double, Integer> tacoAsk = (Map.Entry<Double, Integer>) askLevelsTaco.next();
            Map.Entry<Double, Integer> beefBid = (Map.Entry<Double, Integer>) bidLevelsBeef.next();
            Map.Entry<Double, Integer> tortBid = (Map.Entry<Double, Integer>) bidLevelsTort.next();

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
        Iterator bidLevelsTaco = tacoMaster.getBidLevels().entrySet().iterator();
        Iterator askLevelsBeef = beefMaster.getAskLevels().entrySet().iterator();
        Iterator askLevelsTort = tortMaster.getAskLevels().entrySet().iterator();

        int levelCount = 0; //max depth to search too

        while(bidLevelsTaco.hasNext() && askLevelsBeef.hasNext() && askLevelsTort.hasNext() && levelCount < 5){
            Map.Entry<Double,Integer> tacoBid = (Map.Entry<Double,Integer>)bidLevelsTaco.next();
            Map.Entry<Double,Integer> beefAsk = (Map.Entry<Double,Integer>)askLevelsBeef.next();
            Map.Entry<Double,Integer> tortAsk = (Map.Entry<Double,Integer>)askLevelsTort.next();

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
            tacoMaster.accountOrder(trade.getPrice(), trade.getVolume(), trade.getSide());
        }
        else if(trade.getBook().equals(beefMaster.getSymbol())){
            beefMaster.accountOrder(trade.getPrice(), trade.getVolume(), trade.getSide());
        }
        else{
            tortMaster.accountOrder(trade.getPrice(), trade.getVolume(), trade.getSide());
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


    public int minOfThree(int a, int b, int c){
        return Math.min(a,Math.min(b,c));
    }
}
