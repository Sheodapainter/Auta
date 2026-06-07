package com.umcsuser.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import com.umcsuser.carrent.db.JsonFileStorage;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleCategoryConfigJsonRepository implements VehicleCategoryConfigRepository {
    private final JsonFileStorage<VehicleCategoryConfig> storage = new JsonFileStorage<>("categories.json", new TypeToken<List<VehicleCategoryConfig>>() {}.getType());
    private final List<VehicleCategoryConfig> configs;

    public VehicleCategoryConfigJsonRepository() {
        this.configs = new ArrayList<>(storage.load());
    }
    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> copy = new ArrayList<>();
        for(VehicleCategoryConfig config: configs) {
            copy.add(config.copy());
        }
        return copy;
    }
    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream()
                .filter(c -> c.getCategory() != null)
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst().map(VehicleCategoryConfig::copy);
    }
}
