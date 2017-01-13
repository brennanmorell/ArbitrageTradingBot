package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);
    private static final int maxLongPosition  = 10;
    private static final int maxShortPosition = -10;

    private int beefPosition = 0;
    private int tortPosition = 0;

    public int getMaxLongPosition(){
        return maxLongPosition;
    }

    public int getMaxShortPosition(){
        return maxShortPosition;
    }

    public int getBeefPosition(){
        return beefPosition;
    }

    public int getTortPosition(){
        return tortPosition;
    }

    public void updateBeefPosition(int tradeVolume, Side side){
        beefPosition = (side == Side.BUY) ? beefPosition + tradeVolume : beefPosition - tradeVolume;
        //LOGGER.info("Beef Position: " + beefPosition);
    }

    public void updateTortPosition(int tradeVolume, Side side){
        tortPosition = (side == Side.BUY) ? tortPosition + tradeVolume : tortPosition - tradeVolume;
        //LOGGER.info("Tort Position: " + tortPosition);
    }
}
