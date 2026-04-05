package org.example;

public class User {
    private String login;
    private String password;
    private Role role;
    private String rentedVehicleId;
    public String getLogin() {
        return this.login;
    }
    public String getPassword() {
        return this.password;
    }
    public Role getRole() {
        return this.role;
    }
    public String getRentedVehicleId() {
        return this.rentedVehicleId;
    }
    public void rentVehicle(String vId) {
        this.rentedVehicleId=vId;
    }
    public void returnVehicle() { this.rentedVehicleId=null; }
    public User(String login, String password, Role role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }
    public String toCSV() {
        return login + ";" + password + ";" + role + ";" + rentedVehicleId;
    }
}
