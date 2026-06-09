package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class AuthService implements AuthServiceInterface{
    private final UserRepository repo;

    @Override
    @Transactional
    public boolean register(String username, String password) {
        for(User u: repo.findAll()){
            if(Objects.equals(u.getLogin(), username)) {
                return false;
            }
        }
        repo.save(new User(null, username, BCrypt.hashpw(password, BCrypt.gensalt()), Role.USER));
        return true;
    }

    @Override
    @Transactional
    public Optional<User> login(String username, String password) {
        for(User u: repo.findAll()){
            if(Objects.equals(u.getLogin(), username)) {
                if(BCrypt.checkpw(password, u.getPasswordHash())) {
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }
    public AuthService(UserRepository r) {
        this.repo=r;
    }
}
