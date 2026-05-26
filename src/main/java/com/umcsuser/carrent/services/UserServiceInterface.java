package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.List;

public interface UserServiceInterface {

    List<User> findAll();

    User findById(String id);

    void deleteUser(String id, String loggedUserId);
}