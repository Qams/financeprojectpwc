package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.ActiveMessage;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveControllerTest {

    @InjectMocks
    private ActiveController activeController;

    @Test
    public void shouldGetActiveTraining() {
        // given
        String name = "name";
        String message = "message";
        ActiveMessage activeMessage = new ActiveMessage("name", "message");
        ActiveMessage activeMessage1 = new ActiveMessage();

        // when
        ActiveMessage result = activeController.activeTraining(activeMessage);

        // then
        assertEquals(result, activeMessage);
        assertEquals(result.getMessage(), message);
        assertEquals(result.getName(), name);
    }
}