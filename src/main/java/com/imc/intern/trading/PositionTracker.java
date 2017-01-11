package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by imc on 10/01/2017.
 */
public class PositionTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTracker.class);
    private static int taco_position = 0;
    private static int beef_position = 0
    private static int tort_position = 0;

    private static Symbol symbol_taco;
    private static Symbol symbol_beef;
    private static Symbol symbol_tort;

    public int getTacoPosition(){
        return taco_position;
    }
    public int getBeefPosition(){ return beef_position; }
    public int getTortPosition(){return tort_position; }

    public PositionTracker(Symbol taco, Symbol beef, Symbol tort){
        symbol_taco = taco;
        symbol_beef = beef;
        symbol_tort = tort;
    }


    public void updateTacoPosition(int tradeVolume, Side side){ //how to make this method one-liner?
        if(side == Side.BUY){
            taco_position+=tradeVolume;
        }
        else {
            taco_position-= tradeVolume;
        }
        LOGGER.info("Updated Position: " + taco_position);
    }

    public void updateBeefPosition(int tradeVolume, Side side){ //how to make this method one-liner?
        if(side == Side.BUY){
            beef_position+=tradeVolume;
        }
        else {
            beef_position-= tradeVolume;
        }
        LOGGER.info("Updated Position: " + beef_position);
    }

    public void updateTortPosition(int tradeVolume, Side side){ //how to make this method one-liner?
        if(side == Side.BUY){
            tort_position+=tradeVolume;
        }
        else {
            tort_position-=tradeVolume;
        }
        LOGGER.info("Updated Position: " + tort_position;
    }

}
