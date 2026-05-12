package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentalJsonRepository implements RentalRepository {
    private final JsonFileStorage<Rental> storage = new JsonFileStorage<>("rentals.json", new TypeToken<List<Rental>>() {}.getType());
    private List<Rental> rentals;
    public RentalJsonRepository() {
        this.rentals = new ArrayList<>(storage.load());
    }
    @Override
    public List<Rental> findAll() {
        List<Rental> copy = new ArrayList<>();
        for(Rental rental: rentals) {
            copy.add(rental.copy());
        }
        return copy;
    }
    @Override
    public Optional<Rental> findById(String id) {
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
        storage.save(rentals);
        return toSave.copy();
    }
    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
        storage.save(rentals);
    }
    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId))
                .filter(rental -> rental.getReturnDateTime() == null)
                .findFirst()
                .map(Rental::copy);
    }
}
