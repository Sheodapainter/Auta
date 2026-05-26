package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalService implements RentalServiceInterface{
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Rental rentVehicle(String uid, String vid) {
        boolean userHasActiveRental = rentalRepository.findAll().stream().anyMatch(r -> uid.equals(r.getUserId()) && r.isActive());
        if(userHasActiveRental) {
            throw new IllegalStateException("Masz juz aktywne wypozyczenie.");
        }

        Vehicle vehicle = vehicleRepository.findById(vid).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym id."));
        User user = userRepository.findById(uid).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono uzytkownika o podanym id."));

        boolean vehicleIsRented = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();

        if(vehicleIsRented) {
            throw new IllegalStateException("Ten pojazd jest juz wypozyczony.");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        return rentalRepository.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .filter(Rental::isActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypozyczonego pojazdu."));

        rental.setReturnDateTime(LocalDateTime.now().toString());

        return rentalRepository.save(rental);
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .filter(Rental::isActive)
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .toList();
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}
