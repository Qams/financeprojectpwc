package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.exchange.ExchangeRate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShortDate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateStats;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateWindowed;
import pl.edu.agh.financeservice.service.exchange.ExchangeRateService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateControllerTest {

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @Mock
    private ExchangeRateService exchangeRateService;


    @Test
    public void shouldFindCurrentExchangeRate() {
        // given
        String exchange = "EURUSD";
        Date date = new Date();
        ExchangeRate exchangeRate = new ExchangeRate(exchange, date, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, "EUR", "USD", "123");
        when(exchangeRateService.findCurrentExchangeRate(exchange)).thenReturn(exchangeRate);

        // when
        ExchangeRate result = exchangeRateController.findCurrentExchangeRate(exchange);

        // then
        assertEquals(exchangeRate, result);
        assertEquals(exchangeRate.getTocurrency(), result.getTocurrency());
        assertEquals(exchangeRate.getFromcurrency(), result.getFromcurrency());
        assertEquals(exchangeRate.getId(), result.getId());
        assertEquals(exchangeRate.getCurrencytime(), result.getCurrencytime());
        assertEquals(exchangeRate.getBidvolume(), result.getBidvolume());
        assertEquals(exchangeRate.getAskvolume(), result.getAskvolume());
        assertEquals(exchangeRate.getExchange(), result.getExchange());

    }

    @Test
    public void shouldFindAllCurrentExchangeRate() {
        // given
        List<ExchangeRate> list = Collections.singletonList(new ExchangeRate());
        when(exchangeRateService.findAllCurrentExchangeRate()).thenReturn(list);

        // when
        List<ExchangeRate> result = exchangeRateController.findAllCurrentExchangeRate();

        // then
        assertEquals(result, list);
    }

    @Test
    public void shouldFindWindowedExchangeRate() {
        // given
        String exchange = "EURUSD";
        List<ExchangeRateWindowed> exchangeRateWindowedList = Collections.singletonList(new ExchangeRateWindowed());
        when(exchangeRateService.findWindowedExchangeRate(exchange)).thenReturn(exchangeRateWindowedList);

        // when
        List<ExchangeRateWindowed> result = exchangeRateController.findWindowedExchangeRate(exchange);

        // then
        assertEquals(result, exchangeRateWindowedList);
    }

    @Test
    public void shouldFindExchangeRateDayStats() {
        // given
        String date = "04-05-2019";
        List<ExchangeRateStats> exchangeRateStatsList = Collections.singletonList(new ExchangeRateStats());
        when(exchangeRateService.findExchangeRateDayStats(date)).thenReturn(exchangeRateStatsList);

        // when
        List<ExchangeRateStats> result = exchangeRateController.findExchangeRateDayStats(date);

        // then
        assertEquals(exchangeRateStatsList, result);

    }

    @Test
    public void shouldFindAllDayOpenExchangeRate() {
        // given
        ExchangeRateShortDate exchangeRateShortDate = new ExchangeRateShortDate();
        List<ExchangeRateShortDate> list = Collections.singletonList(exchangeRateShortDate);
        when(exchangeRateService.findAllOpenExchangeRate(any())).thenReturn(list);

        // when
        List<ExchangeRateShortDate> result = exchangeRateController.findAllDayOpenExchangeRate();

        // then
        assertEquals(list, result);
    }

    @Test
    public void shouldFindAllDayOpenExchangeRateWithParam() {
        // given
        String exchange = "EURUSD";
        ExchangeRateShortDate exchangeRateShortDate = new ExchangeRateShortDate();
        when(exchangeRateService.findForExchangeOpenExchangeRate(any(), any(), any())).thenReturn(exchangeRateShortDate);

        // when
        ExchangeRateShortDate result = exchangeRateController.findAllDayOpenExchangeRate(exchange);

        // then
        assertEquals(exchangeRateShortDate, result);
    }

}