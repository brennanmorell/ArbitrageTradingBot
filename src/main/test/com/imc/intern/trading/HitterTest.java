package com.imc.intern.trading;

import com.imc.intern.exchange.datamodel.Side;
import com.imc.intern.exchange.datamodel.api.OrderType;
import com.imc.intern.exchange.datamodel.api.Symbol;
import com.imc.intern.exchange.views.ExchangeView;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.TreeMap;
import java.util.Map;

public class HitterTest {
    // NAJ: I would use an @Before method to define the symbol, hitter, levels as fields:
//    private final static Symbol SYMBOL = Symbol.of("test");
//    private ExchangeView exchangeView;
//    private Map<Double, Integer> levels;
//    private Hitter hitter;
//
//    @Before
//    public void before()
//    {
//        exchangeView = Mockito.mock(ExchangeView.class);
//        hitter = new Hitter(exchangeView);
//        levels = new TreeMap<>();
//    }
//
//    @After
//    public void after()
//    {
//        Mockito.verifyNoMoreInteractions(exchangeView);
//    }
//
//    @Test
//    public void buyStrategySendsOrders()
//    {
//        double price = 18.0;
//        int volume = 100;
//        levels.put(price, volume);
//        hitter.buyStrategy(levels, symbol);
//
//        Mockito.verify(exchangeView).createOrder(SYMBOL, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
//    }



   /* @Test
    public void buyStrategySendsOrders()
    {
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);
        Symbol symbol = Symbol.of("buyOrdersTest");
        double price = 18.0;
        int volume = 100;
        Hitter hitter = new Hitter(exchangeView);
        Map<Double, Integer> levels = new TreeMap<>();
        levels.put(price, volume);

        hitter.buyStrategy(levels, symbol);

        Mockito.verify(exchangeView).createOrder(symbol, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
    }

    @Test
    public void buyStrategyNoOrders() {
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);
        Symbol symbol = Symbol.of("buyNoOrdersTest");
        double price = 22.0;
        int volume = 100;
        Hitter hitter = new Hitter(exchangeView);
        Map<Double, Integer> levels = new TreeMap<>();
        levels.put(price, volume);

        hitter.buyStrategy(levels, symbol);

        Mockito.verifyZeroInteractions(exchangeView);
    }


    @Test
    public void sellStrategySendsOrders(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);
        Symbol symbol = Symbol.of("sellOrdersTest");
        double price = 22.0;
        int volume = 100;
        Hitter hitter = new Hitter(exchangeView);
        Map<Double, Integer> levels = new TreeMap<>();
        levels.put(price, volume);

        hitter.sellStrategy(levels, symbol);

        Mockito.verify(exchangeView).createOrder(symbol, price, volume, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
    }

    @Test
    public void sellStrategyNoOrders(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);
        Symbol symbol = Symbol.of("sellNoOrdersTest");
        double price = 18.0;
        int volume = 100;
        Hitter hitter = new Hitter(exchangeView);
        Map<Double, Integer> levels = new TreeMap<>();
        levels.put(price, volume);

        hitter.sellStrategy(levels, symbol);

        Mockito.verifyZeroInteractions(exchangeView);
    }
    */

    /*@Test
    public void testTacoBuyStrategy(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);

        Mockito.when(tacoMaster.getBidLevels()).thenReturn(
        )
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);
        tacoMaster
    }*/

   /* @Test
    public void testTacoBuy(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

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

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService);
        hitter.tacoBuyStrategy();

        //Verify create and account taco order
        Mockito.verify(executionService).executeBuyTaco(10d, 6d, 5d, 7);
    }

    @Test
    public void testNoTacoBuy(){
        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        ExecutionService executionService = Mockito.mock(ExecutionService.class);

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

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService);
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

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService);
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

        Hitter hitter = new Hitter(tacoMaster, beefMaster, tortMaster, executionService);
        hitter.tacoSellStrategy();

        //Verify no order
        Mockito.verifyZeroInteractions(executionService);
    }
*/
}