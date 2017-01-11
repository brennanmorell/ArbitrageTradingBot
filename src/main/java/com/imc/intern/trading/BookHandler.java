package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.*;
import com.imc.intern.exchange.datamodel.api.Error;
import com.imc.intern.exchange.datamodel.jms.ExposureUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created by imc on 10/01/2017.
 */
public class BookHandler implements OrderBookHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookHandler.class);

    private static List<OwnTrade> ownTrades = new ArrayList<>();

    private static Map<Double, Integer> askLevelsTaco = new TreeMap<>();
    private static Map<Double, Integer> bidLevelsTaco = new TreeMap<>(Collections.reverseOrder());

    private static Map<Double, Integer> askLevelsBeef = new TreeMap<>();
    private static Map<Double, Integer> bidLevelsBeef = new TreeMap<>(Collections.reverseOrder());

    private static Map<Double, Integer> askLevelsTort = new TreeMap<>();
    private static Map<Double, Integer> bidLevelsTort = new TreeMap<>(Collections.reverseOrder());

    private static Hitter hitter;
    private static PositionTracker tracker;
    private static Symbol symbol_taco;
    private static Symbol symbol_beef;
    private static Symbol symbol_tort;

    public BookHandler(Hitter h, PositionTracker t, Symbol taco, Symbol beef, Symbol tort){
        hitter = h;
        tracker = t;
        symbol_taco = taco;
        symbol_beef = beef;
        symbol_tort = tort;
    }

    //Called every 10 seconds and every time a level of the book changes
    @Override
    public void handleRetailState(RetailState retailState) {
        LOGGER.info(retailState.getBook() + " Retail State: " + retailState.toString());

        processLevels(retailState.getBids(), retailState.getAsks(), retailState.getBook());
    }

    //Called every time you make an order, update an order, cancel an order, or your order gets traded
    @Override
    public void handleExposures(ExposureUpdate exposures) {
        LOGGER.info(exposures.getBook() + " Exposures: " + exposures.toString());
    }

    //Called every time you complete a trade
    @Override
    public void handleOwnTrade(OwnTrade trade) {
        LOGGER.info(trade.getBook() + " Trade: " + trade.toString());
        if(trade.getBook() == symbol_taco){
            accountTrade(trade, askLevelsTaco, bidLevelsTaco);
            managePosition(trade, symbol_taco);

        }
        else if(trade.getBook() == symbol_beef){
            accountTrade(trade, askLevelsBeef, bidLevelsBeef);
            managePosition(trade, symbol_beef);
        }
        else{
            accountTrade(trade, askLevelsTort, bidLevelsTort);
            managePosition(trade, symbol_tort);
        }
    }

    //Called when an error occurs
    @Override
    public void handleError(Error e){
        LOGGER.info(e.getBook() + " Error: " + e.toString());
    }

    private static void accountTrade(OwnTrade trade, Map<Double, Integer> askLevels, Map<Double, Integer> bidLevels){
        Double price = trade.getPrice();
        int tradeVolume = trade.getVolume();

        if(trade.getSide() == Side.BUY){
            int currentVolume = askLevels.get(price);
            if(currentVolume - tradeVolume == 0){
                askLevels.remove(price);
            }
            else{
                askLevels.replace(price, currentVolume, currentVolume-tradeVolume);
            }
        }
        else{
            int currentVolume = bidLevels.get(price);
            if(currentVolume-tradeVolume == 0){
                bidLevels.remove(price);
            }
            else{
                bidLevels.replace(price, currentVolume, currentVolume-tradeVolume);
            }
        }

        ownTrades.add(trade); //do i want to keep track of master list of trades or individual lists
    }

    public static void managePosition(OwnTrade trade, Symbol symbol){
        if(symbol == symbol_taco){
            tracker.updateTacoPosition(trade.getVolume(), trade.getSide());
        }
        else if(symbol == symbol_beef){
            tracker.updateBeefPosition(trade.getVolume(), trade.getSide());
        }
        else{
            tracker.updateTortPosition(trade.getVolume(), trade.getSide());
        }
    }

    //Used to update map for provided side (buy or ask)
    private static void updateSide(List<RetailState.Level> side, Map<Double, Integer> levels) {
        for(RetailState.Level a : side) {
            double price = a.getPrice();
            int volume = a.getVolume();

            if(volume == 0) { //indicates a level was filled/removed
                side.remove(price);
            }
            else {
                if(levels.containsKey(price)) { //indicates level was partially filled/updated
                    int oldVolume = levels.get(price);
                    levels.replace(price, oldVolume, volume);
                }
                else {
                    levels.put(price, volume); //indicates new level
                }
            }
        }
    }

    //Used to process level changes in book
    public void processLevels(List<RetailState.Level> bids, List<RetailState.Level> asks, Symbol symbol){
        Map<Double, Integer> askLevels;
        Map<Double, Integer> bidLevels;
        if(symbol == symbol_taco){
            askLevels = askLevelsTaco;
            bidLevels = bidLevelsTaco;
        }
        else if(symbol == symbol_beef){
            askLevels = askLevelsBeef;
            bidLevels = bidLevelsBeef;
        }
        else{
            askLevels = askLevelsTort;
            bidLevels = bidLevelsTort;
        }

        LOGGER.info(symbol + " Bids:");
        Iterator it_b = bidLevels.entrySet().iterator();
        while(it_b.hasNext()) {
            Map.Entry pair = (Map.Entry) it_b.next();
            LOGGER.info(pair.getValue() + "@" + pair.getKey());
        }

       LOGGER.info(symbol + "Current Asks");
        Iterator it_a = askLevels.entrySet().iterator();
        while(it_a.hasNext()){
            Map.Entry pair = (Map.Entry)it_a.next();
            LOGGER.info(pair.getValue() + "@" + pair.getKey());
        }

        hitter.buyStrategy(askLevels, symbol);
        hitter.sellStrategy(bidLevels, symbol);
    }
}
