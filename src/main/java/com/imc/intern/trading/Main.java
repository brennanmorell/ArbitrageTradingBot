package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.print.Book;

/**
 * Created by imc on 10/01/2017.
 */
public class Main
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class); //use info level to log

    private static final String EXCHANGE_URL = "tcp://wintern.imc.com:61616";
    private static final String USERNAME = "bmorell";
    private static final String PASSWORD = "friend planet experiment taught";
    //private static final String BOOK1 = "BMO1";
    private static final String BOOK_TACO = "TACO";
    private static final String BOOK_BEEF = "BEEF";
    private static final String BOOK_TORT = "TORT";


    public static void main(String[] args) throws Exception
    {
        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        final RemoteExchangeView exchangeView = client.getExchangeView();

        Hitter hitter = new Hitter(exchangeView);
        PositionTracker tracker = new PositionTracker();
        BookHandler b = new BookHandler(hitter, tracker, Symbol.of(BOOK_TACO));
        exchangeView.subscribe(Symbol.of(BOOK_TACO), b);
        exchangeView.subscribe(Symbol.of(BOOK_BEEF), b);
        exchangeView.subscribe(Symbol.of(BOOK_TORT), b);

        client.start();
    }
}
