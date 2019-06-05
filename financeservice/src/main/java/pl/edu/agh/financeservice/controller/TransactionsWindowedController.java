package pl.edu.agh.financeservice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pl.edu.agh.financeservice.model.transactions.TransactionsWindowed;

@Controller
public class TransactionsWindowedController {

    @MessageMapping("/finance")
    @SendTo("/topic/transactions")
    public TransactionsWindowed financeStats(TransactionsWindowed message) {
        System.out.println("FINANCE");
        return message;
    }
}
