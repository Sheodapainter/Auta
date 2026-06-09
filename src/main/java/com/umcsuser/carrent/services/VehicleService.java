package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> rentalRepository
                        .findByVehicleIdAndReturnDateIsNull(v.getId())
                        .isEmpty())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle findById(String id) {
        Optional<Vehicle> v = vehicleRepository.findById(id);
        return v.orElse(null);
    }

    @Override
    @Transactional
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void removeVehicle(String id) {
        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(id).isEmpty()) {
            vehicleRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Nie można usunąć wypożyczonego pojazdu!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleRented(String vehicleId) {
        Optional<Rental> r = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
        return r.isPresent();
    }
}
