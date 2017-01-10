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