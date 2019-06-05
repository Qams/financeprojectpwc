package pl.edu.agh.financeservice.repository.cassandra;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.agh.financeservice.model.transactions.FinTransaction;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<FinTransaction, String> {
    @Query("SELECT * FROM fintransaction WHERE ftowner = :ftowner")
    List<FinTransaction> findAll(@Param("ftowner") String ftowner);

    @Query("SELECT SUM(ftamount) FROM fintransaction WHERE ftowner = :ftowner")
    BigDecimal findAccountBalanceForOwner(@Param("ftowner") String ftowner);

    @Query("SELECT * FROM fintransaction WHERE ftowner = :ftowner AND fttimestamp > :fttimestamp")
    List<FinTransaction> findTransactionForOwnerAfterDate(@Param("ftowner") String ftowner,
                                                          @Param("fttimestamp") long fttimestamp);

    @Query("SELECT SUM(ftamount) FROM fintransaction WHERE ftowner = :ftowner AND fttimestamp > :fttimestamp")
    BigDecimal findAccountBalanceForOwnerAfterDate(@Param("ftowner") String ftowner,
                                                   @Param("fttimestamp") Long fttimestamp);
}
