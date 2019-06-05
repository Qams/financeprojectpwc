package pl.edu.agh.financeservice.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByUsername(String username);
}

