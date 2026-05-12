package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.Optional;

public class VehicleService {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalService rentalService;

    public VehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, RentalService rentalService) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }
    public boolean removeVehicle(String id) {
        if (rentalService.findByVehicleIdAndReturnDateIsNull(id).isEmpty()) {
            vehicleRepository.deleteById(id);
            return true;
        } else {
            throw new IllegalArgumentException("Nie można usunąć wypożyczonego pojazdu!");
        }
    }
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }
}
