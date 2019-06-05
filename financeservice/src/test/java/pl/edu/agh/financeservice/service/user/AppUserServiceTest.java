package pl.edu.agh.financeservice.service.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.repository.postgres.AppUserRepository;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppUserServiceTest {

    @InjectMocks
    private AppUserService appUserService;

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    public void shouldLoadUserByUsername() {
        // given
        String username = "username";
        AppUser appUser = new AppUser(1L, "pass", "usrname", "fname", "sname", "role", Collections.emptyList());
        when(appUserRepository.findAppUserByUsername(username)).thenReturn(appUser);

        // when
        UserDetails result = appUserService.loadUserByUsername(username);

        // then
        assertEquals(result.getPassword(), appUser.getPassword());
        assertEquals(result.getUsername(), appUser.getUsername());
    }
}