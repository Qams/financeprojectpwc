package pl.edu.agh.financeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.financeservice.model.exchange.ExchangeRate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateShortDate;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateStats;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateWindowed;
import pl.edu.agh.financeservice.service.exchange.ExchangeRateService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/exchange")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping(value = "/{exchange}/current")
    public ExchangeRate findCurrentExchangeRate(@PathVariable("exchange") String exchange) {
        return exchangeRateService.findCurrentExchangeRate(exchange);
    }

    @GetMapping(value = "/all/current")
    public List<ExchangeRate> findAllCurrentExchangeRate() {
        return exchangeRateService.findAllCurrentExchangeRate();
    }

    @GetMapping(value = "/{exchange}/windowed")
    public List<ExchangeRateWindowed> findWindowedExchangeRate(@PathVariable("exchange") String exchange) {
        return exchangeRateService.findWindowedExchangeRate(exchange);
    }

    @GetMapping(value = "/stats/{date}")
    public List<ExchangeRateStats> findExchangeRateDayStats(@PathVariable("date") String date) {
        // TODO rate stats from batch layer (processed data)
        return exchangeRateService.findExchangeRateDayStats(date);
    }

    @GetMapping(value = "/open")
    public List<ExchangeRateShortDate> findAllDayOpenExchangeRate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        String today = dateFormat.format(new Date());
        return exchangeRateService.findAllOpenExchangeRate(today);
    }

    @GetMapping(value = "/open/{exchange}")
    public ExchangeRateShortDate findAllDayOpenExchangeRate(@PathVariable("exchange") String exchange) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        String today = dateFormat.format(new Date());
        return exchangeRateService.findForExchangeOpenExchangeRate(today, exchange.substring(0,3), exchange.substring(3,6));
    }
}
