package org.example;
import java.util.List;

public interface IVehicleRepository {
    boolean rentVehicle(String id);
    boolean returnVehicle(String id);
    List<Vehicle> getVehicles();
    Vehicle getVehicle(String id);
    boolean add(Vehicle vehicle);
    boolean remove(String id);
    void save();
    void load();
}