package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.RentalData;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<RentalData> storage;
    private List<RentalData> rawRentals;
    private List<Rental> rentals;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    public RentalJsonRepository(@Value("${carrent.json.rentals-file}") String filename, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<RentalData>>() {}.getType());
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.rawRentals = new ArrayList<>(storage.load());
        this.rentals = new ArrayList<>();
        for(RentalData data: rawRentals) {
            Vehicle vehicle = vehicleRepository.findById(data.getVehicleId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Vehicle not found: " + data.getVehicleId()));
            User user = userRepository.findById(data.getUserId())
                    .orElseThrow(() -> new IllegalStateException(
                            "User not found: " + data.getUserId()));
            Rental r = new Rental(data.getId(), vehicle, user, data.getRentDateTime(), data.getReturnDateTime());
            rentals.add(r);
        }
    }
    @Override
    public List<Rental> findAll() {
        updateRentals();
        List<Rental> copy = new ArrayList<>();
        for(Rental rental: rentals) {
            copy.add(rental.copy());
        }
        return copy;
    }
    @Override
    public Optional<Rental> findById(String id) {
        updateRentals();
        return rentals.stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst()
                .map(Rental::copy);
    }
    @Override
    public Rental save(Rental rental) {
        if(rental == null) {
            throw new IllegalArgumentException("rental cannot be null");
        }
        Rental toSave = rental.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        } else {
            rentals.removeIf(r -> r.getId().equals(toSave.getId()));
        }
        rentals.add(toSave);
        RentalData rd = new RentalData(toSave.getId(), toSave.getVehicleId(), toSave.getUserId(), toSave.getRentDateTime(), toSave.getReturnDateTime());
        rawRentals.add(rd);
        storage.save(rawRentals);
        return toSave.copy();
    }
    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
        rawRentals.removeIf(rental -> rental.getId().equals(id));
        storage.save(rawRentals);
    }
    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        updateRentals();
        return rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId))
                .filter(rental -> rental.getReturnDateTime() == null)
                .findFirst()
                .map(Rental::copy);
    }
    private void updateRentals() {
        List<RentalData> toDelete = new ArrayList<>();
        for(RentalData rental: rawRentals) {
            if(userRepository.findById(rental.getUserId()).isEmpty()||vehicleRepository.findById(rental.getVehicleId()).isEmpty()) {
                toDelete.add(rental);
            }
        }
        for(RentalData rental: toDelete) {
            rentals.removeIf(r -> r.getId().equals(rental.getId()));
            rawRentals.remove(rental);
        }
        storage.save(rawRentals);
    }
}
