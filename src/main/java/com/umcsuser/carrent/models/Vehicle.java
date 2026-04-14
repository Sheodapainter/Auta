package com.umcsuser.carrent;
public abstract class Vehicle {
    private String id;
    private String brand;
    private String model;
    private Integer year;
    private Double price;
    private Boolean rented;
    public String toCSV() {
        return id+";"+brand+";"+model+";"+year+";"+price+";"+rented;
    }
    public String getId() {
        return this.id;
    }
    public String getBrand() {
        return this.brand;
    }
    public String getModel() {
        return this.model;
    }
    public Integer getYear() {
        return this.year;
    }
    public Double getPrice() {
        return this.price;
    }
    public void setRented(Boolean rented) {
        this.rented = rented;
    }
    public Boolean isRented() {
        return this.rented;
    }
    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", rented=" + rented +
                '}';
    }

    public Vehicle(Boolean rented, Double price, Integer year, String model, String brand, String id) {
        this.rented = rented;
        this.price = price;
        this.year = year;
        this.model = model;
        this.brand = brand;
        this.id = id;
    }
    public Vehicle(Vehicle other) {
        this.rented = other.isRented();
        this.price = other.getPrice();
        this.year = other.getYear();
        this.model = other.getModel();
        this.brand = other.getBrand();
        this.id = other.getId();
    }
}
