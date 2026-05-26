package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.Optional;

public class VehicleService implements VehicleServiceInterface{

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public VehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        List<Rental> rented = rentalRepository.findAll().stream().filter(r -> r.getReturnDateTime().isBlank()).toList();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for(Rental r: rented) {
            Vehicle v = r.getVehicle();
            vehicles.remove(v);
        }
        return vehicles;
    }

    @Override
    public Vehicle findById(String id) {
        Optional<Vehicle> v = vehicleRepository.findById(id);
        return v.orElse(null);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }

    @Override
    public void removeVehicle(String id) {
        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(id).isEmpty()) {
            vehicleRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Nie można usunąć wypożyczonego pojazdu!");
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        Optional<Rental> r = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
        return r.isPresent();
    }
}
