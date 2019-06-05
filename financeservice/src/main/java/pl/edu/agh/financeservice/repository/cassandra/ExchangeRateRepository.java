package pl.edu.agh.financeservice.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.model.exchange.ExchangeRate;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, String> {
    @Query("SELECT * FROM exchangerate WHERE exchange = :exchange")
    List<ExchangeRate> findAll(@Param("exchange") String exchange);

    @Query("SELECT * FROM exchangerate WHERE exchange = :exchange LIMIT 1")
    ExchangeRate findCurrentExchangeRate(@Param("exchange") String exchange);
}
