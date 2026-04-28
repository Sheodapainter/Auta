package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.Optional;

public class AuthService {
    private UserRepository repo;
    public Boolean register(String username, String password) {
        for(User u: repo.findAll()){
            if(Objects.equals(u.getLogin(), username)) {
                return false;
            }
        }
        repo.save(new User(null, username, BCrypt.hashpw(password, BCrypt.gensalt()), Role.USER));
        return true;
    }
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
