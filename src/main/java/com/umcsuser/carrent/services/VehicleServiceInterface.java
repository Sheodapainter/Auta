package com.umcsuser.carrent.services;


import com.umcsuser.carrent.models.Vehicle;

import java.util.List;

public interface VehicleServiceInterface {

    List<Vehicle> findAllVehicles();

    List<Vehicle> findAvailableVehicles();

    Vehicle findById(String id);

    Vehicle addVehicle(Vehicle vehicle);

    void removeVehicle(String vehicleId);

    boolean isVehicleRented(String vehicleId);
}