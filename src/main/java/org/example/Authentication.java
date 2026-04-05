package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;

public class Authentication {
    private IUserRepository repo;
    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
    public User authenticate(String username, String password) {
        for(User u: repo.getUsers()){
            if(Objects.equals(u.getLogin(), username)) {
                if(Objects.equals(u.getPassword(), hashPassword(password))) {
                    return u;
                }
            }
        }
        return null;
    }
    public Authentication(IUserRepository r) {
        this.repo=r;
    }
    public IUserRepository getRepo() {
        return repo;
    }
}
