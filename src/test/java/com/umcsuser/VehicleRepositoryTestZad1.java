package org.example;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRepositoryTestZad1 {

    @Test
    void getVehiclesShouldReturnDeepCopy() {
        IVehicleRepository repo = new VehicleRepositoryImpl();
        List<Vehicle> vehicles1 = repo.getVehicles();
        List<Vehicle> vehicles2  = repo.getVehicles();
        assertNotSame(vehicles1, vehicles2);
        assertNotSame(vehicles1.get(0), vehicles2.get(0));
    }

    @Test
    void addingToReturnedListShouldNotChangeRepository() {
        IVehicleRepository repo = new VehicleRepositoryImpl();
        List<Vehicle> vehicles = repo.getVehicles();
        int repoSizeBefore = repo.getVehicles().size();
        vehicles.add(new Car("100", "Test", "Test", 2026, 1.0, false));
        int repoSizeAfter = repo.getVehicles().size();
        assertEquals(repoSizeBefore, repoSizeAfter);
    }

    @Test
    void changingReturnedVehicleShouldNotChangeRepository() {
        IVehicleRepository repo = new VehicleRepositoryImpl();
        List<Vehicle> vehicles = repo.getVehicles();
        Vehicle copy = vehicles.get(0);
        boolean rented = repo.getVehicles().get(0).isRented();
        copy.setRented(!copy.isRented());
        boolean repoRentedAfterChange = repo.getVehicles().get(0).isRented();
        assertEquals(rented, repoRentedAfterChange);
    }
}