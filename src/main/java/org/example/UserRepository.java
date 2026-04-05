package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository implements IUserRepository{
    private List<User> userList = new ArrayList<>();
    private UI inter;
    @Override
    public User getUser(String login) {
        for(User u: userList) {
            if(Objects.equals(u.getLogin(), login)) {
                return new User(u.getLogin(), u.getPassword(), u.getRole(), u.getRentedVehicleId());
            }
        }
        return null;
    }
    @Override
    public List<User> getUsers() {
        List<User> h = new ArrayList<>();
        for(User u: userList) {
            h.add(new User(u.getLogin(), u.getPassword(), u.getRole(), u.getRentedVehicleId()));
        }
        return h;
    }
    @Override
    public boolean update(User user) {
        if(user.getRentedVehicleId()==inter.currentUser().getRentedVehicleId()) {
            return false;
        } else {
            inter.currentUser().rentVehicle(user.getRentedVehicleId());
            return true;
        }
    }
    @Override
    public void load() {
        try {
            File file = new File("users.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            userList.clear();
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(";", -1);
                userList.add(new User(lines[0], lines[1], Role.valueOf(lines[2]), lines[3]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void save() {
        try (PrintWriter out = new PrintWriter("users.csv")) {
            for(User u: userList) {
                out.print(u.toCSV());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void addUser(User user) {
        userList.add(user);
    }
    public UserRepository() {
        load();
    }
    public void setInter(UI inter) {
        this.inter = inter;
    }
    public UI getInter() {
        return inter;
    }
}
