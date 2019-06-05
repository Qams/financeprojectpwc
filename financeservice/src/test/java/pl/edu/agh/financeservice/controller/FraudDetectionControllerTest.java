package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.FraudPaysimTransaction;
import pl.edu.agh.financeservice.service.paysim.FraudTransactionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FraudDetectionControllerTest {

    @InjectMocks
    private FraudDetectionController fraudDetectionController;

    @Mock
    private FraudTransactionService fraudTransactionService;


    @Test
    public void shouldReturnFraudTransactions() {
        // given
        FraudPaysimTransaction fraudPaysimTransaction = new FraudPaysimTransaction("1", BigDecimal.ONE, 1.3);
        when(fraudTransactionService.findFraudTransactions()).thenReturn(Arrays.asList(
            fraudPaysimTransaction
        ));

        // when
        List<FraudPaysimTransaction> result = fraudDetectionController.getPaysimFraudTransactions();

        // then
        assertEquals(result.get(0).getAmount(), fraudPaysimTransaction.getAmount());
        assertEquals(result.get(0).getId(), fraudPaysimTransaction.getId());
        assertEquals(result.get(0).getProbability(), fraudPaysimTransaction.getProbability(), 1.0);
    }
}