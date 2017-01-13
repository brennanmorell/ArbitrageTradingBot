package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;

public class HitterTest {

    @Test
    public void testTacoBuy(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoAskLevels = new TreeMap<>();
        tacoAskLevels.put(10d, 9);
        Mockito.when(tacoMaster.getAskLevels()).thenReturn(tacoAskLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefBidLevels = new TreeMap<>();
        beefBidLevels.put(6d, 7);
        Mockito.when(beefMaster.getBidLevels()).thenReturn(beefBidLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortBidLevels = new TreeMap<>();
        tortBidLevels.put(5d, 10);
        Mockito.when(tortMaster.getBidLevels()).thenReturn(tortBidLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.tacoBuyStrategy();

        //Verify create and account taco order
        Mockito.verify(executionService).executeBuyTaco(10d, 6d, 5d, 7);
    }

    @Test
    public void testFlattenLong(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        Map<Double, Integer> beefBidLevels = new TreeMap<>(Collections.reverseOrder());
        beefBidLevels.put(10d, 30);
        beefBidLevels.put(9d, 40);
        Mockito.when(beefMaster.getBidLevels()).thenReturn(beefBidLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.flattenLong(60, beefMaster);

        //should sell 30@10d and then 30@9d
        InOrder inOrder = Mockito.inOrder(executionService);
        inOrder.verify(executionService).executeFlatten(beefMaster.getSymbol(), 10d, 30, Side.SELL);
        inOrder.verify(executionService).executeFlatten(beefMaster.getSymbol(), 9d, 30, Side.SELL);
    }

    @Test
    public void testFlattenShort(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        Map<Double, Integer> beefAskLevels = new TreeMap<>();
        beefAskLevels.put(9d, 30);
        beefAskLevels.put(10d, 40);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefAskLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.flattenShort(-60, beefMaster);

        //should buy 30@9d and then 30@10d
        InOrder inOrder = Mockito.inOrder(executionService);
        inOrder.verify(executionService).executeFlatten(beefMaster.getSymbol(), 9d, 30, Side.BUY);
        inOrder.verify(executionService).executeFlatten(beefMaster.getSymbol(), 10d, 30, Side.BUY);
    }


    @Test
    public void testNoTacoBuy(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoAskLevels = new TreeMap<>();
        tacoAskLevels.put(10d, 20);
        Mockito.when(tacoMaster.getBidLevels()).thenReturn(tacoAskLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefBidLevels = new TreeMap<>();
        beefBidLevels.put(4d, 10);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefBidLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortBidLevels = new TreeMap<>();
        tortBidLevels.put(5d, 10);
        Mockito.when(tortMaster.getAskLevels()).thenReturn(tortBidLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.tacoBuyStrategy();

        //Verify create and account taco order
        Mockito.verifyZeroInteractions(executionService);
    }

    @Test
    public void testTacoSell(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoBidLevels = new TreeMap<>();
        tacoBidLevels.put(10d, 9);
        Mockito.when(tacoMaster.getBidLevels()).thenReturn(tacoBidLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefAskLevels = new TreeMap<>();
        beefAskLevels.put(4d, 11);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefAskLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortAskLevels = new TreeMap<>();
        tortAskLevels.put(5d, 11);
        Mockito.when(tortMaster.getAskLevels()).thenReturn(tortAskLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.tacoSellStrategy();

        //Verify create and account taco order
        Mockito.verify(executionService).executeSellTaco(10d, 4d, 5d, 9);
    }

    @Test
    public void testNoTacoSell(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

        PositionTracker tracker = Mockito.mock(PositionTracker.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoBidLevels = new TreeMap<>();
        tacoBidLevels.put(10d, 10);
        Mockito.when(tacoMaster.getBidLevels()).thenReturn(tacoBidLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefAskLevels = new TreeMap<>();
        beefAskLevels.put(6d, 10);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefAskLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortAskLevels = new TreeMap<>();
        tortAskLevels.put(5d, 12);
        Mockito.when(tortMaster.getAskLevels()).thenReturn(tortAskLevels);

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService, tracker);
        hitter.tacoSellStrategy();

        //Verify no order
        Mockito.verifyZeroInteractions(executionService);
    }
}