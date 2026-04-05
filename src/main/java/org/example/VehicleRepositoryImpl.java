package org.example;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehicleRepositoryImpl implements IVehicleRepository{
    private List<Vehicle> vehicleList = new ArrayList<>();
    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v: vehicleList) {
            if(Objects.equals(v.getId(), id)) {
                v.setRented(true);
                return true;
            }
        }
        return false;
    }
    public boolean returnVehicle(String id) {
        for (Vehicle v: vehicleList) {
            if(Objects.equals(v.getId(), id)) {
                v.setRented(false);
                return true;
            }
        }
        return false;
    }
    public List<Vehicle> getVehicles() {
        List<Vehicle> c = new ArrayList<>();
        for(Vehicle v : vehicleList) {
            if(v.getClass()==Motorcycle.class) {
                c.add(new Motorcycle(v.getId(), v.getBrand(), v.getModel(), v.getYear(), v.getPrice(), v.isRented(), ((Motorcycle) v).getKategoria()));
            } else {
                c.add(new Car(v.getId(), v.getBrand(), v.getModel(), v.getYear(), v.getPrice(), v.isRented()));
            }
        }
        return c;
    }
    public void save() {
        try (PrintWriter out = new PrintWriter("vehicles.csv")) {
            for(Vehicle v: vehicleList) {
                out.print(v.toCSV());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void load() {
        try {
            File file = new File("vehicles.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            vehicleList.clear();
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(";");
                if(Objects.equals(lines[0], "CAR")) {
                    vehicleList.add(new Car(lines[1], lines[2], lines[3],
                            Integer.parseInt(lines[4]),
                            Double.parseDouble(lines[5]),
                            Boolean.parseBoolean(lines[6])));
                } else {
                    MotorcycleCategory k = MotorcycleCategory.valueOf(lines[7]);
                    vehicleList.add(new Motorcycle(lines[1], lines[2], lines[3],
                            Integer.parseInt(lines[4]),
                            Double.parseDouble(lines[5]),
                            Boolean.parseBoolean(lines[6]), k));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Vehicle getVehicle(String id) {
        int i=0;
        for(Vehicle v: vehicleList) {
            if(Objects.equals(v.getId(), id)) {
                return vehicleList.get(i);
            }
            i++;
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        if(vehicleList.contains(vehicle)) {
            return false;
        }
        vehicleList.add(vehicle);
        return true;
    }

    @Override
    public boolean remove(String id) {
        for(Vehicle v: vehicleList) {
            if(Objects.equals(v.getId(), id)) {
                vehicleList.remove(v);
                return true;
            }
        }
        return false;
    }

    public VehicleRepositoryImpl() {
        load();
    }
}
