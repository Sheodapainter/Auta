package org.example;
public class Car extends Vehicle {
    public Car(String id, String brand, String model, Integer year, Double price, Boolean rented) {
        super(rented, price, year, model, brand, id);
    }
    public Car(Car other) {
        super(other);
    }
}
