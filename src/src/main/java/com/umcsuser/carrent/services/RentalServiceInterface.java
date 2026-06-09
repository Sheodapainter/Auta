package com.umcsuser.carrent.services;


import com.umcsuser.carrent.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalServiceInterface {

    Rental rentVehicle(String userId, String vehicleId);

    Rental returnVehicle(String userId);

    Optional<Rental> findActiveRentalByUserId(String userId);

    List<Rental> findAllRentals();

    List<Rental> findUserRentals(String userId);

    boolean userHasActiveRental(String userId);

    boolean vehicleHasActiveRental(String vehicleId);
}