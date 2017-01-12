package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);

    private int beefPosition = 0;
    private int tortPosition = 0;

    public int getBeefPosition(){
        return beefPosition;
    }

    public int getTortPosition(){
        return tortPosition;
    }

    public void updateBeefPosition(int tradeVolume, Side side){
        beefPosition = (side == Side.BUY) ? beefPosition + tradeVolume : beefPosition - tradeVolume;
        LOGGER.info("Beef Position: " + beefPosition);
    }

    public void updateTortPosition(int tradeVolume, Side side){
        tortPosition = (side == Side.BUY) ? tortPosition + tradeVolume : tortPosition - tradeVolume;
        LOGGER.info("Tort Position: " + tortPosition);
    }
}
