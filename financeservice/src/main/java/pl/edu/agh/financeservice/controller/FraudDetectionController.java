package pl.edu.agh.financeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.financeservice.model.FraudPaysimTransaction;
import pl.edu.agh.financeservice.service.paysim.FraudTransactionService;

import java.util.List;

@RestController
@RequestMapping("/paysim/fraud")
public class FraudDetectionController {

    @Autowired
    private FraudTransactionService fraudTransactionService;

    @RequestMapping("")
    public List<FraudPaysimTransaction> getPaysimFraudTransactions() {
        return fraudTransactionService.findFraudTransactions();
    }
}
