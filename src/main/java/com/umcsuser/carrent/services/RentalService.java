package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.List;
import java.util.Optional;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final RentalValidator rentalValidator;

    public RentalService(RentalRepository rentalRepository, RentalValidator rentalValidator) {
        this.rentalRepository = rentalRepository;
        this.rentalValidator = rentalValidator;
    }

    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
    public Optional<Rental> findById(String id) {
        return rentalRepository.findById(id);
    }
    public Rental save(Rental rental) {
        rentalValidator.validateRental(rental);
        rentalRepository.save(rental);
        return rental;
    }
    public void deleteById(String id) {
        rentalRepository.deleteById(id);
    }
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String id) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(id);
    }
}
