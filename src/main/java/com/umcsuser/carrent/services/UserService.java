package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService implements UserServiceInterface{
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public UserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        Optional<User> u = userRepository.findById(id);
        return u.orElse(null);
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        if(id==null) {
            throw new IllegalArgumentException("Id nie może być null!");
        }
        if(Objects.equals(id, loggedUserId)) {
            throw new IllegalArgumentException("Nie można usunąć samego siebie!");
        }
        Optional<Rental> r = rentalRepository.findAll().stream()
                .filter(rental -> rental.getUserId().equals(id))
                .filter(rental -> rental.getReturnDateTime()==null).findFirst();
        if(r.isPresent()) {
            throw new IllegalArgumentException("Użytkownik posiada wypożyczony pojazd!");
        }
        userRepository.deleteById(id);
    }
}
