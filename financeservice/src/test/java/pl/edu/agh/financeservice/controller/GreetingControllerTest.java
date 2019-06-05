package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.HelloMessage;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShort;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GreetingControllerTest {

    @InjectMocks
    private GreetingController greetingController;

    @Test
    public void test() {
        // given
        double ask = 1.0;
        double bid = 1.2;
        String exchange = "EURUSD";
        HelloMessage helloMessage = new HelloMessage("id", new Date(), ask, bid, 1.0, 1.0, "EUR", "USD", exchange);
        ExchangeRateShort exchangeRateShort = new ExchangeRateShort();

        // when
        ExchangeRateShort result = greetingController.greeting(helloMessage);

        // then
        assertEquals(result.getAsk(), (Double) helloMessage.getAsk());
        assertEquals(result.getBid(), (Double) helloMessage.getBid());
        assertEquals(result.getExchange(), helloMessage.getExchange());
    }
}