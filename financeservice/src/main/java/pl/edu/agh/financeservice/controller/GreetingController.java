package pl.edu.agh.financeservice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pl.edu.agh.financeservice.model.HelloMessage;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShort;

import java.util.Date;

@Controller
public class GreetingController {

    // TODO TEST CLASS

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ExchangeRateShort greeting(HelloMessage message) {
        System.out.println("HELLO");
        System.out.println("WS " + message.getId() + ", DATE: " + new Date().getTime());
        return new ExchangeRateShort(message.getId(), message.getExchange(), message.getAsk(), message.getBid());
    }
}