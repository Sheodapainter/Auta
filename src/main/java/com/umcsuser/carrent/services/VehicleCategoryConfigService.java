package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }
    public List<VehicleCategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }
    public VehicleCategoryConfig getByCategory(String category) {
        return configRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu" + category));
    }
    public boolean categoryExists(String category) {
        return configRepository.findByCategory(category).isPresent();
    }
}
