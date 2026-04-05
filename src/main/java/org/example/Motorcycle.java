package org.example;
public class Motorcycle extends Vehicle {
    MotorcycleCategory kategoria;
    @Override
    public String toCSV() {
        return super.toCSV()+";"+kategoria;
    }
    public MotorcycleCategory getKategoria() {
        return this.kategoria;
    }
    public Motorcycle(String id, String brand, String model, Integer year, Double price, Boolean rented, MotorcycleCategory kategoria) {
        super(rented, price, year, model, brand, id);
        this.kategoria=kategoria;
    }
    public Motorcycle(Motorcycle other) {
        super(other);
        this.kategoria=other.getKategoria();
    }
}
