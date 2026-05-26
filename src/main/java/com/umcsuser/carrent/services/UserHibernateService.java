package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.HibernateConfig;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.impl.RentalHibernateRepository;
import com.umcsuser.carrent.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserHibernateService implements UserServiceInterface{
    private final UserHibernateRepository userRepository;
    private final RentalHibernateRepository rentalRepository;

    public UserHibernateService(UserHibernateRepository userRepository, RentalHibernateRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return userRepository.findAll();
        }
    }

    @Override
    public User findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            Optional<User> u = userRepository.findById(id);
            return u.orElse(null);
        }
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (id == null) {
                throw new IllegalArgumentException("Id nie może być null!");
            }
            if (Objects.equals(id, loggedUserId)) {
                throw new IllegalArgumentException("Nie można usunąć samego siebie!");
            }
            Optional<Rental> r = rentalRepository.findAll().stream()
                    .filter(rental -> rental.getUserId().equals(id))
                    .filter(rental -> rental.getReturnDateTime() == null).findFirst();
            if (r.isPresent()) {
                throw new IllegalArgumentException("Użytkownik posiada wypożyczony pojazd!");
            }
            userRepository.deleteById(id);
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    private void setSession(Session session) {
        rentalRepository.setSession(session);
        userRepository.setSession(session);
    }
    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
