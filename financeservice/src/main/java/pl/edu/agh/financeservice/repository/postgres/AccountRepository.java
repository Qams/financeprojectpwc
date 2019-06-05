package pl.edu.agh.financeservice.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.financeservice.model.user.postgres.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByNumber(String number);
}
