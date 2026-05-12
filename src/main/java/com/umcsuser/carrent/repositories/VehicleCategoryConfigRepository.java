package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.VehicleCategoryConfig;

import java.util.List;
import java.util.Optional;

public interface VehicleCategoryConfigRepository {
    List<VehicleCategoryConfig> findAll();
    Optional<VehicleCategoryConfig> findByCategory(String category);
}
