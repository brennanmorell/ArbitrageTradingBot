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

    @Test
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
}