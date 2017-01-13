package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.RetailState;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


public class BookMaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookMaster.class);

    private Symbol symbol;
    private Map<Double, Integer> askLevels = new TreeMap<>();
    private Map<Double, Integer> bidLevels = new TreeMap<>(Collections.reverseOrder());

    private Map<Side, List<Long>> outstandingOrders = new HashMap<>();

    public BookMaster(Symbol s){
        symbol = s;
    }

    public Symbol getSymbol(){
        return symbol;
    }

    public Map<Double, Integer> getBidLevels(){
        return bidLevels;
    }

    public Map<Double, Integer> getAskLevels(){
        return askLevels;
    }

    //Used to process level changes in book
    public void processLevels(List<RetailState.Level> bids, List<RetailState.Level> asks) {
        updateSide(bids, bidLevels);
        updateSide(asks, askLevels);

        //logBook();
    }

    //Used to update map for provided side (buy or ask)
    private static void updateSide(List<RetailState.Level> side, Map<Double, Integer> levels) {
        for (RetailState.Level a : side) {
            double price = a.getPrice();
            int volume = a.getVolume();

            if (volume == 0) { //indicates a level was filled/removed
                side.remove(price);
            } else {
                if (levels.containsKey(price)) { //indicates level was partially filled/updated
                    int oldVolume = levels.get(price);
                    levels.replace(price, oldVolume, volume);
                } else {
                    levels.put(price, volume); //indicates new level
                }
            }
        }
    }

    public void updateBooks(Double price, int tradeVolume, Side side) {
        if (side == Side.BUY) {
            int currentVolume = askLevels.get(price);
            if (currentVolume - tradeVolume == 0) {
                askLevels.remove(price);
            } else {
                askLevels.replace(price, currentVolume, currentVolume - tradeVolume);
            }
        } else {
            int currentVolume = bidLevels.get(price);
            if (currentVolume - tradeVolume == 0) {
                bidLevels.remove(price);
            } else {
                bidLevels.replace(price, currentVolume, currentVolume - tradeVolume);
            }
        }
    }

    public void logBook(){
        LOGGER.info(symbol + " Current Bids:");
        for (Map.Entry<Double, Integer> entry : bidLevels.entrySet()){
            LOGGER.info(entry.getValue() + "@" + entry.getKey());
        }

        System.out.println();

        LOGGER.info(symbol + " Current Asks:");
        for (Map.Entry<Double, Integer> entry : askLevels.entrySet()){
            LOGGER.info(entry.getValue() + "@" + entry.getKey());
        }
    }
}
