package pl.edu.agh.financeservice.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.model.exchange.ExchangeRateWindowed;

import java.util.List;


@Repository
public interface ExchangeRateWindowedRepository extends CrudRepository<ExchangeRateWindowed, String> {

    @Query("SELECT * FROM exchangeratewindowed WHERE exchange = :exchange")
    List<ExchangeRateWindowed> findAll(@Param("exchange") String exchange);
}
