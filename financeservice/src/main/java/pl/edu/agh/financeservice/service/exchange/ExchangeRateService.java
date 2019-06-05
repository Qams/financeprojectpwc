package pl.edu.agh.financeservice.service.exchange;

import org.apache.spark.sql.AnalysisException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.financeservice.model.exchange.*;
import pl.edu.agh.financeservice.repository.cassandra.ExchangeRateRepository;
import pl.edu.agh.financeservice.repository.cassandra.ExchangeRateWindowedRepository;
import pl.edu.agh.financeservice.repository.hdfs.ExchangeRateHdfsRepository;

import java.util.*;

@Service
public class ExchangeRateService {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private ExchangeRateHdfsRepository exchangeRateHdfsRepository;

    @Autowired
    private ExchangeRateWindowedRepository exchangeRateWindowedRepository;

    public List<ExchangeRateWindowed> findWindowedExchangeRate(String exchange) {
        return exchangeRateWindowedRepository.findAll(exchange);
    }

    public ExchangeRate findCurrentExchangeRate(String exchange) {
        return exchangeRateRepository.findCurrentExchangeRate(exchange);
    }

    public List<ExchangeRate> findAllCurrentExchangeRate() {
        return Arrays.asList(
                exchangeRateRepository.findCurrentExchangeRate("EURUSD"),
                exchangeRateRepository.findCurrentExchangeRate("EURGBP"),
                exchangeRateRepository.findCurrentExchangeRate("EURCHF"),
                exchangeRateRepository.findCurrentExchangeRate("GBPUSD"),
                exchangeRateRepository.findCurrentExchangeRate("GBPCHF"),
                exchangeRateRepository.findCurrentExchangeRate("USDCHF")
        );
    }

    public ExchangeRateShortDate findForExchangeOpenExchangeRate(String date, String from, String to) {
        try {
            List<ExchangeRateShortDate> exchangeRateDayRates = exchangeRateHdfsRepository.getExchangeRateDayRates(date, from, to);
            return exchangeRateDayRates.get(0);
        } catch (AnalysisException e) {
            System.out.println("EXCEPTION");
        }
        return null;
    }

    public List<ExchangeRateShortDate> findAllOpenExchangeRate(String date) {
        try {
            return Arrays.asList(
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "USD").get(0),
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "GBP").get(0),
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "EUR", "CHF").get(0),
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "GBP", "USD").get(0),
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "GBP", "CHF").get(0),
                    exchangeRateHdfsRepository.getExchangeRateDayRates(date, "USD", "CHF").get(0)
            );
        } catch (AnalysisException e) {
            System.out.println("EXCEPTION");
        }
        return Collections.emptyList();
    }

    public List<ExchangeRateStats> findExchangeRateDayStats(String date) {
//        return exchangeRateHdfsRepository.getExchangeRateDayStats(date, exchange.substring(0,3), exchange.substring(3,6));
        return exchangeRateHdfsRepository.getAdvanceExchangeRateDayStats(date);
    }

    public void setExchangeRateRepository(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void setExchangeRateHdfsRepository(ExchangeRateHdfsRepository exchangeRateHdfsRepository) {
        this.exchangeRateHdfsRepository = exchangeRateHdfsRepository;
    }

    public void setExchangeRateWindowedRepository(ExchangeRateWindowedRepository exchangeRateWindowedRepository) {
        this.exchangeRateWindowedRepository = exchangeRateWindowedRepository;
    }
}
