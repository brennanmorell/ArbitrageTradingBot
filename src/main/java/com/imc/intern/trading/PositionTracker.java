package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NAJ: You can edit your templates to ignore generating this below:
/**
 * Created by imc on 10/01/2017.
 */
public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);
    private static int position = 0;

    // NAJ: I would focus on one bit of code at a time and complete it. So here, while you have a method getPosition, it is not being used.
    // NAJ: This is more a style of programming, its easier to do one thing at a time, commit, and move on to next task iteratively.
    // NAJ: But, if you work best this way, you do you.
    public int getPosition(){
        return position;
    }

    public void updatePosition(int tradeVolume, Side side){ //how to make this method one-liner?
        // NAJ: one-liner: position = side == Side.BUY ? position + tradeVolume : position - tradeVolume;
        if(side == Side.BUY){
            position+=tradeVolume; // NAJ: I would be consistent with spacing here and the else cause
        }
        else {
            position -= tradeVolume;
        }
        LOGGER.info("Updated Position: " + position);
    }
}
