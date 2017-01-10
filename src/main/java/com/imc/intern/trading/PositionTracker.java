package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by imc on 10/01/2017.
 */
public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);
    private static int position = 0;

    public int getPosition(){
        return position;
    }

    public void updatePosition(int tradeVolume, Side side){ //how to make this method one-liner?
        if(side == Side.BUY){
            position+=tradeVolume;
        }
        else {
            position -= tradeVolume;
        }
        LOGGER.info("Updated Position: " + position);
    }
}
