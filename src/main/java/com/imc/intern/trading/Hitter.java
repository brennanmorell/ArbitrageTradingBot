package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OwnTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;



public class Hitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hitter.class);

    // NAJ: would make these values not final and parameterized per book, and possibly a hitter per book.
    /*private static final double bookValue = 20;
    private static final double fixedOffset = .10;
    private static double variableOffset = 0;*/

    private static int maxLongPosition  = 50;
    private static int maxShortPosition = -50;

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

        if(beefPosition > maxLongPosition && tortPosition > maxLongPosition){
            LOGGER.info("Positions are beyond max long. Adjusting positions.");
            flattenLong(beefPosition, tortPosition);
            return false;
        }
        else if(beefPosition < maxShortPosition && tortPosition < maxShortPosition){
            LOGGER.info("Position are beyond max short. Adjusting positions.");
            flattenShort(beefPosition, tortPosition);
            return false;
        }
        else{
            return true;
        }
    }

    public void flattenLong(int beefPosition, int tortPosition){
        LOGGER.info("Attempting to flatten long beef position.");
        Iterator bidLevelsBeef = beefMaster.getBidLevels().entrySet().iterator();
        while(beefPosition - maxLongPosition > 0 && bidLevelsBeef.hasNext()){
            Map.Entry<Double, Integer> beefBid = (Map.Entry<Double, Integer>)bidLevelsBeef.next();
            int volume = Math.min(beefBid.getValue(), beefPosition-maxLongPosition); //makes sure not to over sell and end up short
            executionService.executeBeef(beefBid.getKey(), volume, Side.SELL);
            beefPosition-=volume;
        }

        LOGGER.info("Attempting to flatten long tort position.");
        Iterator bidLevelsTort = tortMaster.getBidLevels().entrySet().iterator();
        while(tortPosition - maxLongPosition > 0 && bidLevelsTort.hasNext()){
            Map.Entry<Double, Integer> tortBid = (Map.Entry<Double, Integer>)bidLevelsTort.next();
            int volume = Math.min(tortBid.getValue(), tortPosition-maxLongPosition); //makes sure not to over sell and end up short
            executionService.executeTort(tortBid.getKey(), volume, Side.SELL);
            tortPosition-=volume;
        }
    }

    public void flattenShort(int beefPosition, int tortPosition){
        LOGGER.info("Attempting to flatten short beef position.");
        Iterator askLevelsBeef = beefMaster.getAskLevels().entrySet().iterator();
        while(maxLongPosition - beefPosition > 0 && askLevelsBeef.hasNext()){
            Map.Entry<Double, Integer> beefAsk = (Map.Entry<Double, Integer>)askLevelsBeef.next();
            int volume = Math.min(beefAsk.getValue(), maxShortPosition-beefPosition); //makes sure not to over sell and end up long
            executionService.executeBeef(beefAsk.getKey(), volume, Side.BUY);
            beefPosition+=volume;
        }

        LOGGER.info("Attempting to flatten short tort position.");
        Iterator askLevelsTort = tortMaster.getAskLevels().entrySet().iterator();
        while(maxLongPosition - tortPosition > 0 && askLevelsTort.hasNext()){
            Map.Entry<Double, Integer> tortAsk = (Map.Entry<Double, Integer>)askLevelsTort.next();
            int volume = Math.min(tortAsk.getValue(), maxShortPosition-tortPosition); //makes sure not to over sell and end up long
            executionService.executeTort(tortAsk.getKey(), volume, Side.BUY);
            tortPosition+=volume;
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
            volume = Math.min(volume, 10); //imposed volume limit
            if (tacoAsk.getKey() < (beefBid.getKey() + tortBid.getKey())) {
                executionService.executeBuyTaco(tacoAsk.getKey(), beefBid.getKey(), tortBid.getKey(), volume);
                //accountTacoBuy(tacoAsk.getKey(), beefBid.getKey(), tortBid.getKey(), volume);
                //break;
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
            volume = Math.min(volume, 10);
            if(tacoBid.getKey() > (beefAsk.getKey() + tortAsk.getKey())){
                executionService.executeSellTaco(tacoBid.getKey(), beefAsk.getKey(), tortAsk.getKey(), volume);
                //accountTacoSell(tacoBid.getKey(), beefAsk.getKey(), tortAsk.getKey(), volume);
                //break;
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
    }


    public int minOfThree(int a, int b, int c){
        return Math.min(a,Math.min(b,c));
    }
}
