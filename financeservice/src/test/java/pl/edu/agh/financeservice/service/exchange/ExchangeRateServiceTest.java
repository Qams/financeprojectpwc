package pl.edu.agh.financeservice.service.exchange;

import org.apache.spark.sql.AnalysisException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.exchange.ExchangeRate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShortDate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateStats;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateWindowed;
import pl.edu.agh.financeservice.repository.cassandra.ExchangeRateRepository;
import pl.edu.agh.financeservice.repository.cassandra.ExchangeRateWindowedRepository;
import pl.edu.agh.financeservice.repository.hdfs.ExchangeRateHdfsRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateHdfsRepository exchangeRateHdfsRepository;

    @Mock
    private ExchangeRateWindowedRepository exchangeRateWindowedRepository;

    @Test
    public void shouldInvokeFindAll() {
        // given
        String currency = "EUR";
        List<ExchangeRateWindowed> list = Collections.singletonList(
                new ExchangeRateWindowed("EUR", new Date(), 1L, 1.0, 1.0, 1.0, "1", "1")
        );
        when(exchangeRateWindowedRepository.findAll(currency)).thenReturn(list);

        // when
        List<ExchangeRateWindowed> result = exchangeRateService.findWindowedExchangeRate(currency);

        // then
        assertEquals(list, result);
        assertEquals(list.get(0).getChanges(), result.get(0).getChanges());
        assertEquals(list.get(0).getEndtime(), result.get(0).getEndtime());
        assertEquals(list.get(0).getExchange(), result.get(0).getExchange());
        assertEquals(list.get(0).getMaxask(), result.get(0).getMaxask());
        assertEquals(list.get(0).getMaxbid(), result.get(0).getMaxbid());
        assertEquals(list.get(0).getMinask(), result.get(0).getMinask());
        assertEquals(list.get(0).getMinbid(), result.get(0).getMinbid());
        assertEquals(list.get(0).getStarttime(), result.get(0).getStarttime());

    }

    @Test
    public void shouldFindCurrentExchangeRate() {
        // given
        String currency = "EUR";
        ExchangeRate exchangeRate = new ExchangeRate("EURUSD", new Date(), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, "EUR", "USD", "11");
        when(exchangeRateRepository.findCurrentExchangeRate(currency)).thenReturn(exchangeRate);

        // when
        ExchangeRate result = exchangeRateService.findCurrentExchangeRate(currency);

        // then
        assertEquals(exchangeRate, result);
        assertEquals(exchangeRate.getExchange(), result.getExchange());
        assertEquals(exchangeRate.getAsk(), result.getAsk());
        assertEquals(exchangeRate.getAskvolume(), result.getAskvolume());
        assertEquals(exchangeRate.getBid(), result.getBid());
        assertEquals(exchangeRate.getBidvolume(), result.getBidvolume());
        assertEquals(exchangeRate.getCurrencytime(), result.getCurrencytime());
        assertEquals(exchangeRate.getFromcurrency(), result.getFromcurrency());
        assertEquals(exchangeRate.getId(), result.getId());
        assertEquals(exchangeRate.getFromcurrency(), result.getFromcurrency());
        assertEquals(exchangeRate.getTocurrency(), result.getTocurrency());

    }



    @Test
    public void shouldFindAllCurrentExchangeRate() {
        // given
        String currency = "EUR";
        ExchangeRate exchangeRate = new ExchangeRate();
        when(exchangeRateRepository.findCurrentExchangeRate("EURUSD")).thenReturn(exchangeRate);
        when(exchangeRateRepository.findCurrentExchangeRate("EURGBP")).thenReturn(exchangeRate);
        when(exchangeRateRepository.findCurrentExchangeRate("EURCHF")).thenReturn(exchangeRate);
        when(exchangeRateRepository.findCurrentExchangeRate("GBPUSD")).thenReturn(exchangeRate);
        when(exchangeRateRepository.findCurrentExchangeRate("GBPCHF")).thenReturn(exchangeRate);
        when(exchangeRateRepository.findCurrentExchangeRate("USDCHF")).thenReturn(exchangeRate);

        // when
        List<ExchangeRate> result = exchangeRateService.findAllCurrentExchangeRate();

        // then
        assertEquals(result.size(), 6);

    }

    @Test
    public void shouldFindForExchangeOpenExchangeRate() throws AnalysisException {
        // given
        String to = "USD";
        String from = "EUR";
        String date = "2018-01-03";
        ExchangeRateShortDate exchange = new ExchangeRateShortDate();
        List<ExchangeRateShortDate> dateList = Collections.singletonList(
                new ExchangeRateShortDate("EURUSD", 1.0, 1.0, 1L)
        );
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, from, to)).thenReturn(dateList);


        // when
        ExchangeRateShortDate result = exchangeRateService.findForExchangeOpenExchangeRate(date, from, to);

        // then
        assertEquals(result.getAsk(), dateList.get(0).getAsk());
        assertEquals(result.getBid(), dateList.get(0).getBid());
        assertEquals(result.getExchange(), dateList.get(0).getExchange());
        assertEquals(result.getTimestamp(), dateList.get(0).getTimestamp());

    }

    @Test
    public void shouldFindAllOpenExchangeRate() throws AnalysisException {
        // given
        String to = "USD";
        String from = "EUR";
        String date = "2018-01-03";
        ExchangeRateShortDate exchange = new ExchangeRateShortDate();
        List<ExchangeRateShortDate> dateList = Collections.singletonList(
                new ExchangeRateShortDate("EURUSD", 1.0, 1.0, 1L)
        );
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "USD")).thenReturn(dateList);
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "GBP")).thenReturn(dateList);
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "CHF")).thenReturn(dateList);
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "GBP", "USD")).thenReturn(dateList);
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "GBP", "CHF")).thenReturn(dateList);
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "USD", "CHF")).thenReturn(dateList);


        // when
        List<ExchangeRateShortDate> result = exchangeRateService.findAllOpenExchangeRate(date);

        // then
        assertEquals(result.size(), 6);

    }

    @Test
    public void shouldFindAllOpenExchangeRateWithException() throws AnalysisException {
        // given
        String to = "USD";
        String from = "EUR";
        String date = "2018-01-03";
        ExchangeRateShortDate exchange = new ExchangeRateShortDate();
        List<ExchangeRateShortDate> dateList = Collections.singletonList(
                new ExchangeRateShortDate("EURUSD", 1.0, 1.0, 1L)
        );
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "USD")).thenThrow(AnalysisException.class);

        // when
        List<ExchangeRateShortDate> result = exchangeRateService.findAllOpenExchangeRate(date);

        // then
        assertEquals(result.size(), 0);

    }

    @Test
    public void shouldFindForExchangeOpenExchangeRateThrowException() throws AnalysisException {
        // given
        String to = "USD";
        String from = "EUR";
        String date = "2018-01-03";
        List<ExchangeRateShortDate> dateList = Collections.singletonList(
                new ExchangeRateShortDate("EURUSD", 1.0, 1.0, 1L)
        );
        when(exchangeRateHdfsRepository.getExchangeRateDayRates(date, from, to)).thenThrow(AnalysisException.class);

        ExchangeRateShortDate result = exchangeRateService.findForExchangeOpenExchangeRate(date, from, to);

        assertNull(result);
    }

    @Test
    public void shouldFindExchangeRateDayStats() throws AnalysisException {
        // given
        String date = "2018-01-03";
        List<ExchangeRateStats> dateList = Collections.singletonList(
            new ExchangeRateStats(1.0, 1.0, 1.0, 1.0, 4, "2018-03-04", "EURUSD", 5, 6, 2, 1, 1.0, 1.4, 1.5, 1.4)
        );
        when(exchangeRateHdfsRepository.getAdvanceExchangeRateDayStats(date)).thenReturn(dateList);

        List<ExchangeRateStats> result = exchangeRateService.findExchangeRateDayStats(date);

        assertEquals(result, dateList);
        assertEquals(result.get(0).getChanges(), dateList.get(0).getChanges());
        assertEquals(result.get(0).getCloseAsk(), dateList.get(0).getCloseAsk(), 0.1);
        assertEquals(result.get(0).getCloseBid(), dateList.get(0).getCloseBid(), 0.1);
        assertEquals(result.get(0).getDay(), dateList.get(0).getDay());
        assertEquals(result.get(0).getExchange(), dateList.get(0).getExchange());
        assertEquals(result.get(0).getMaxask(), dateList.get(0).getMaxask(), 0.1);
        assertEquals(result.get(0).getMaxaskdate(), dateList.get(0).getMaxaskdate());
        assertEquals(result.get(0).getMaxbid(), dateList.get(0).getMaxbid(), 0.1);
        assertEquals(result.get(0).getMaxbiddate(), dateList.get(0).getMaxbiddate());
        assertEquals(result.get(0).getMinask(), dateList.get(0).getMinask(), 0.1);
        assertEquals(result.get(0).getMinaskdate(), dateList.get(0).getMinaskdate());
        assertEquals(result.get(0).getOpenAsk(), dateList.get(0).getOpenAsk(), 0.1);
        assertEquals(result.get(0).getMinbid(), dateList.get(0).getMinbid(), 0.1);
        assertEquals(result.get(0).getMinbiddate(), dateList.get(0).getMinbiddate());
        assertEquals(result.get(0).getOpenBid(), dateList.get(0).getOpenBid(), 0.1);
    }

}