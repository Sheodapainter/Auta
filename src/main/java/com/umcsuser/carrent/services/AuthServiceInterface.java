package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.Optional;

public interface AuthServiceInterface {

    boolean register(String username, String password);

    Optional<User> login(String username, String password);
}