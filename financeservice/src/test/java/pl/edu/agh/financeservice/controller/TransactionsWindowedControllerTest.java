package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.transactions.TransactionsWindowed;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionsWindowedControllerTest {

    @InjectMocks
    private TransactionsWindowedController transactionsWindowedController;

    @Test
    public void shouldFindFinanceStats() {
        // given
        TransactionsWindowed transactionsWindowed = new TransactionsWindowed("EUR", 1.0, 1.1, 1L, 2L, 123L, 1234L);

        // when
        TransactionsWindowed result = transactionsWindowedController.financeStats(transactionsWindowed);

        // then
        assertEquals(result, transactionsWindowed);
        assertEquals(result.getCurrency(), transactionsWindowed.getCurrency());
        assertEquals(result.getDateEnd(), transactionsWindowed.getDateEnd());
        assertEquals(result.getDateStart(), transactionsWindowed.getDateStart());
        assertEquals(result.getIncomeSumAmount(), transactionsWindowed.getIncomeSumAmount());
        assertEquals(result.getIncomeTransactions(), transactionsWindowed.getIncomeTransactions());
        assertEquals(result.getOutcomeTransactions(), transactionsWindowed.getOutcomeTransactions());
        assertEquals(result.getOutcomeSumAmount(), transactionsWindowed.getOutcomeSumAmount());
    }
}