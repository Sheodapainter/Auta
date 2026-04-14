package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository{
    private JsonFileStorage<User> storage;
    private List<User> users;

    public UserJsonRepository() {
        this.storage = new JsonFileStorage<>("users.json", User.class);
        this.users = new ArrayList<>();
    }
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }
    @Override
    public User save(User user) {
        Optional<User> existingUser = findById(user.getId());
        if (existingUser.isPresent()) {
            users.remove(existingUser.get());
        }
        users.add(user);
        storage.save(users);
        return user;
    }
    @Override
    public void deleteById(String id) {
        Optional<User> existingUser = findById(id);
        if (existingUser.isPresent()) {
            users.remove(existingUser.get());
        }
        storage.save(users);
    }
}
