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

    @Test
    public void testTacoBuy(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);

        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoAskLevels = new TreeMap<>();
        tacoAskLevels.put(10d, 50);
        Mockito.when(tacoMaster.getAskLevels()).thenReturn(tacoAskLevels);


        //Set up beefMaster map
        Map<Double, Integer> beefBidLevels = new TreeMap<>();
        beefBidLevels.put(7d, 75);
        Mockito.when(beefMaster.getBidLevels()).thenReturn(beefBidLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortBidLevels = new TreeMap<>();
        tortBidLevels.put(5d, 75);
        Mockito.when(tortMaster.getBidLevels()).thenReturn(tortBidLevels);

        Hitter hitter = new Hitter(exchangeView, tacoMaster, beefMaster, tortMaster);
        hitter.tacoBuyStrategy();

        //Verify create and account taco order
        Mockito.verify(exchangeView).createOrder(tacoMaster.getSymbol(), 10d, 50, OrderType.IMMEDIATE_OR_CANCEL, Side.BUY);
        Mockito.verify(tacoMaster).accountOrder(10d, 50, Side.BUY);

        //Verify account beef and tort orders
        Mockito.verify(beefMaster).accountOrder(7d, 50, Side.SELL);
        Mockito.verify(tortMaster).accountOrder(5d, 50, Side.SELL);
    }

    @Test
    public void testNoTacoBuy(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);

        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoBidLevels = new TreeMap<>();
        tacoBidLevels.put(10d, 50);
        Mockito.when(tacoMaster.getAskLevels()).thenReturn(tacoBidLevels);


        //Set up beefMaster map
        Map<Double, Integer> beefBidLevels = new TreeMap<>();
        beefBidLevels.put(5d, 75);
        Mockito.when(beefMaster.getBidLevels()).thenReturn(beefBidLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortBidLevels = new TreeMap<>();
        tortBidLevels.put(4d, 75);
        Mockito.when(tortMaster.getBidLevels()).thenReturn(tortBidLevels);

        Hitter hitter = new Hitter(exchangeView, tacoMaster, beefMaster, tortMaster);
        hitter.tacoBuyStrategy();

        //Verify no order occurs
        Mockito.verifyZeroInteractions(exchangeView);
    }

    @Test
    public void testTacoSell(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);

        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoBidLevels = new TreeMap<>();
        tacoBidLevels.put(10d, 50);
        Mockito.when(tacoMaster.getBidLevels()).thenReturn(tacoBidLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefAskLevels = new TreeMap<>();
        beefAskLevels.put(5d, 75);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefAskLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortAskLevels = new TreeMap<>();
        tortAskLevels.put(4d, 75);
        Mockito.when(tortMaster.getAskLevels()).thenReturn(tortAskLevels);

        Hitter hitter = new Hitter(exchangeView, tacoMaster, beefMaster, tortMaster);
        hitter.tacoSellStrategy();

        //Verify create and account taco order
        Mockito.verify(exchangeView).createOrder(tacoMaster.getSymbol(), 10d, 50, OrderType.IMMEDIATE_OR_CANCEL, Side.SELL);
        Mockito.verify(tacoMaster).accountOrder(10d, 50, Side.SELL);

        //Verify account beef and tort orders
        Mockito.verify(beefMaster).accountOrder(5d, 50, Side.BUY);
        Mockito.verify(tortMaster).accountOrder(4d, 50, Side.BUY);
    }

    @Test
    public void testNoTacoSell(){
        ExchangeView exchangeView = Mockito.mock(ExchangeView.class);

        BookMaster tacoMaster = Mockito.mock(BookMaster.class);
        BookMaster beefMaster = Mockito.mock(BookMaster.class);
        BookMaster tortMaster = Mockito.mock(BookMaster.class);

        //Set up tacoMaster map
        Map<Double, Integer> tacoBidLevels = new TreeMap<>();
        tacoBidLevels.put(10d, 50);
        Mockito.when(tacoMaster.getBidLevels()).thenReturn(tacoBidLevels);

        //Set up beefMaster map
        Map<Double, Integer> beefAskLevels = new TreeMap<>();
        beefAskLevels.put(7d, 75);
        Mockito.when(beefMaster.getAskLevels()).thenReturn(beefAskLevels);

        //Set up tacoMaster map
        Map<Double, Integer> tortAskLevels = new TreeMap<>();
        tortAskLevels.put(5d, 75);
        Mockito.when(tortMaster.getAskLevels()).thenReturn(tortAskLevels);

        Hitter hitter = new Hitter(exchangeView, tacoMaster, beefMaster, tortMaster);
        hitter.tacoSellStrategy();

        //Verify create and account taco order
        Mockito.verifyZeroInteractions(exchangeView);
    }

}