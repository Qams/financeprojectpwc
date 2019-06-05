package pl.edu.agh.financeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.financeservice.model.user.postgres.AppUser;
import pl.edu.agh.financeservice.repository.postgres.AppUserRepository;
import pl.edu.agh.financeservice.service.user.AppUserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @RequestMapping
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    @RequestMapping("/name/{username}")
    public AppUser findUserByUsername(@PathVariable String username) {
        return appUserRepository.findAppUserByUsername(username);
    }

}
