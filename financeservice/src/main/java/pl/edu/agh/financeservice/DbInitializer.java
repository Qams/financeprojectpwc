package pl.edu.agh.financeservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.financeservice.model.user.postgres.Account;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.model.user.postgres.Currency;
import pl.edu.agh.financeservice.repository.postgres.AccountRepository;
import pl.edu.agh.financeservice.repository.postgres.AppUserRepository;

import java.util.*;

@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void run(String... strings) {
        initUsers();
        System.out.println(" -- Database has been initialized");
    }

    private void initUsers() {

        Account account = Account.builder()
                .currency(Currency.EUR)
                .number("11000011112222333300004444")
                .build();

        Account account2 = Account.builder()
                .currency(Currency.USD)
                .number("11333355552222333300008888")
                .build();

        List<Account> accounts1 = new ArrayList<>();
        accounts1.add(account);

        List<Account> accounts2 = new ArrayList<>();
        accounts2.add(account2);

        AppUser user1 = AppUser.builder()
                .username("usertest")
                .password(new BCryptPasswordEncoder().encode("hello"))
                .firstName("John")
                .surname("Smith")
                .role("admin")
                .accounts(accounts1)
                .build();

        AppUser user2 = AppUser.builder()
                .username("usertest2")
                .password(new BCryptPasswordEncoder().encode("asd"))
                .firstName("Mark")
                .surname("Jones")
                .role("user")
                .accounts(accounts2)
                .build();

        account.setAppUser(user1);
        account2.setAppUser(user2);

        accountRepository.save(account);
        accountRepository.save(account2);

        appUserRepository.save(user1);
        appUserRepository.save(user2);

    }
}
