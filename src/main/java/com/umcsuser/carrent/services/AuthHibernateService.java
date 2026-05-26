package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.HibernateConfig;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface{
    private final UserHibernateRepository repo;
    public boolean register(String username, String password) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            for (User u : repo.findAll()) {
                if (Objects.equals(u.getLogin(), username)) {
                    return false;
                }
            }
            repo.save(new User(UUID.randomUUID().toString(), username, BCrypt.hashpw(password, BCrypt.gensalt()), Role.USER));
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }
    public Optional<User> login(String username, String password) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            for (User u : repo.findAll()) {
                if (Objects.equals(u.getLogin(), username)) {
                    if (BCrypt.checkpw(password, u.getPasswordHash())) {
                        return Optional.of(u);
                    }
                }
            }
            return Optional.empty();
        }
    }
    public AuthHibernateService(UserHibernateRepository r) {
        this.repo=r;
    }
    private void setSession(Session session) {
        repo.setSession(session);
    }
    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
