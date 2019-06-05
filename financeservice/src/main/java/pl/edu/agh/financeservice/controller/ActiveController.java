package pl.edu.agh.financeservice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pl.edu.agh.financeservice.model.ActiveMessage;

@Controller
public class ActiveController {

    // TODO TEST CLASS

    @MessageMapping("/active")
    @SendTo("/topic/training")
    public ActiveMessage activeTraining(ActiveMessage message) {
        System.out.println("HELLO FROM WEBSOCKET");
        return message;
    }
}
