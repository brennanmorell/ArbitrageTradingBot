package com.imc.intern.trading;

import com.imc.intern.exchange.client.ExchangeClient;
import com.imc.intern.exchange.client.RemoteExchangeView;
import com.imc.intern.exchange.datamodel.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String EXCHANGE_URL = "tcp://54.227.125.23:61616";
    private static final String USERNAME = "bmorell";
    private static final String PASSWORD = "friend planet experiment taught";
    private static final String BOOK_TACO = "TACO";
    private static final String BOOK_BEEF = "BEEF";
    private static final String BOOK_TORT = "TORT";


    public static void main(String[] args) throws Exception
    {
        LOGGER.info("Let's make money."); // msanders: lol, nice.
        ExchangeClient client = ExchangeClient.create(EXCHANGE_URL, Account.of(USERNAME), PASSWORD);
        final RemoteExchangeView exchangeView = client.getExchangeView();

        PositionTracker tracker = new PositionTracker();

        BookMaster tacoBookMaster = new BookMaster(Symbol.of(BOOK_TACO));
        BookMaster beefBookMaster = new BookMaster(Symbol.of(BOOK_BEEF));
        BookMaster tortBookMaster = new BookMaster(Symbol.of(BOOK_TORT));

        ExecutionService executionService = new ExecutionService(exchangeView);

        Hitter hitter = new Hitter(tacoBookMaster, beefBookMaster, tortBookMaster, executionService, tracker);

        BookHandler tacoBookHandler = new BookHandler(tacoBookMaster, hitter);
        BookHandler beefBookHandler = new BookHandler(beefBookMaster, hitter);
        BookHandler tortBookHandler = new BookHandler(tortBookMaster, hitter);

        client.start();

        exchangeView.subscribe(Symbol.of(BOOK_TACO), tacoBookHandler);
        exchangeView.subscribe(Symbol.of(BOOK_BEEF), beefBookHandler);
        exchangeView.subscribe(Symbol.of(BOOK_TORT), tortBookHandler);

        /*Quoting Strategy that requires manual intervention but makes bank when people rush to get flat. Also is low risk for exposure
        because I quote low volume so won't end up too short or long if not all quotes are hit. Essentially, when everyone is rushing to
        get flat, they don't check prices and just buy/sell blindly (I do the same). So I quote down the depth and just wait to be hit,
        then cancel once i am hit.*/

        //hitter.quoteTort();
        //hitter.quoteBeef();
       // hitter.withdrawFromMarket();
    }
}
