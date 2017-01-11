package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);

    private int position = 0;

    public void updatePosition(int tradeVolume, Side side){
        position = side == Side.BUY ? position + tradeVolume : position - tradeVolume;
        LOGGER.info("Updated Position: " + position);
    }

}
