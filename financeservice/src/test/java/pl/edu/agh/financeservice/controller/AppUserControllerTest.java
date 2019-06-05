package pl.edu.agh.financeservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.agh.financeservice.model.user.postgres.Account;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.model.user.postgres.Currency;
import pl.edu.agh.financeservice.repository.postgres.AppUserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppUserControllerTest {

    @InjectMocks
    private AppUserController appUserController;

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    public void shouldFindAllUsers() {
        // given
        List<AppUser> appUsers = Collections.singletonList(new AppUser(1L, "pass", "usrname", "fname", "sname", "admin", Collections.emptyList()));
        when(appUserRepository.findAll()).thenReturn(
                appUsers
        );

        // when
        List<AppUser> result = appUserController.findAll();

        // then
        assertEquals(appUsers.get(0).getId(), result.get(0).getId());
        assertEquals(appUsers.get(0).getAccounts(), result.get(0).getAccounts());
        assertEquals(appUsers.get(0).getAuthorities(), result.get(0).getAuthorities());
        assertEquals(appUsers.get(0).getFirstName(), result.get(0).getFirstName());
        assertEquals(appUsers.get(0).getPassword(), result.get(0).getPassword());
        assertEquals(appUsers.get(0).getRole(), result.get(0).getRole());
        assertEquals(appUsers.get(0).getSurname(), result.get(0).getSurname());
        assertEquals(appUsers.get(0).getUsername(), result.get(0).getUsername());
        assertEquals(appUsers.get(0).isAccountNonExpired(), result.get(0).isAccountNonExpired());
        assertEquals(appUsers.get(0).isAccountNonLocked(), result.get(0).isAccountNonLocked());
        assertEquals(appUsers.get(0).isCredentialsNonExpired(), result.get(0).isCredentialsNonExpired());
        assertEquals(appUsers.get(0).isEnabled(), result.get(0).isEnabled());

    }

    @Test
    public void shouldFineUserByName() {
        // given
        String username = "user";
        AppUser appUser = new AppUser();
        Account account = new Account(1L, "123", Currency.USD, appUser);
        appUser.setAccounts(Collections.singletonList(account));
        when(appUserRepository.findAppUserByUsername(username)).thenReturn(appUser);

        // when
        AppUser result = appUserController.findUserByUsername(username);

        // then
        assertEquals(result, appUser);
        assertEquals(result.getAccounts().get(0).getAppUser(), appUser);
        assertEquals(result.getAccounts().get(0).getCurrency(), Currency.USD);
        assertEquals(result.getAccounts().get(0).getNumber(), "123");
        assertEquals(result.getAccounts().get(0).getId(), 1L);

    }
}