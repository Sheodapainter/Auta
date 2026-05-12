package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, UserValidator userValidator, RentalService rentalService) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.rentalService = rentalService;
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
    public User findByLogin(String login) {
        if(userRepository.findByLogin(login).isEmpty()) {
            throw new IllegalArgumentException("Nie ma użytkownika o danym loginie.");
        }
        return userRepository.findByLogin(login).get();
    }
    public User findById(String id) {
        if(userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Nie ma użytkownika o danym loginie.");
        }
        return userRepository.findById(id).get();
    }
    public void deleteById(String id) {
        List<Rental> rentals = rentalService.findAll();
        for(Rental r: rentals) {
            if(Objects.equals(r.getUserId(), id)) {
                if(r.getReturnDateTime()==null) {
                    throw new IllegalArgumentException("Użytkownik ma wypożyczony pojazd!");
                }
            }
        }
        userRepository.deleteById(id);
        System.out.println("Użytkownik usunięty pomyślnie.");
    }
}
