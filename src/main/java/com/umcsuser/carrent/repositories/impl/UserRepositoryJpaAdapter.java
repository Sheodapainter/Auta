package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserJpaRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class UserRepositoryJpaAdapter implements UserRepository {

    private final UserJpaRepository delegate;

    public UserRepositoryJpaAdapter(UserJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<User> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Optional<User> user = delegate.findAll().stream().filter(u -> Objects.equals(u.getLogin(), login)).findFirst();
        return user.flatMap(value -> delegate.findById(value.getId()));
    }

    @Override
    public User save(User user) {
        if(user.getId()==null) {
            user.setId(UUID.randomUUID().toString());
        }
        return delegate.save(user);
    }

    @Override
    public void deleteById(String id) {
        delegate.deleteById(id);
    }
}
