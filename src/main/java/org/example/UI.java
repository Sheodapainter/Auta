package org.example;

import java.util.List;
import java.util.Scanner;

public class UI {
    private User loggedIn = null;
    private Authentication auth;
    private IVehicleRepository vehicleRepository;
    private IUserRepository userRepository;
    private Scanner scanner = new Scanner(System.in);
    public User currentUser() {
        return loggedIn;
    }
    public boolean login() {
        System.out.println("Podaj login użytkownika:");
        String log = scanner.nextLine();
        System.out.println("Podaj hasło:");
        String pass = scanner.nextLine();
        loggedIn = auth.authenticate(log, pass);
        if(loggedIn == null) {
            System.out.println("Niepoprawne dane logowania");
            return false;
        } else {
            loggedIn = userRepository.getUser(log);
            System.out.println("Zalogowano "+log);
            return true;
        }
    }
    public boolean logout() {
        if(loggedIn == null) {
            System.out.println("Nie ma zalogowanego uzytkownika");
            return false;
        } else {
            loggedIn = null;
            System.out.println("Wylogowano pomyślnie");
            return true;
        }
    }
    public boolean register() {
        if(loggedIn == null) {
            System.out.println("Podaj nazwę użytkownika: ");
            String username = scanner.nextLine();
            if(username.contains(" ")) {
                System.out.println("Nazwa nie może zawierać spacji");
                return false;
            }
            for(User u: auth.getRepo().getUsers()) {
                if(username.equals(u.getLogin())) {
                    System.out.println("Nazwa użytkownika zajęta");
                    return false;
                }
            }
            System.out.println("Nazwa użytkownika poprawna. Podaj hasło: ");
            String password = scanner.nextLine();
            userRepository.addUser(new User(username, auth.hashPassword(password), Role.USER, null));
            loggedIn = userRepository.getUser(username);
            return true;
        } else {
            System.out.println("Posiadasz już dane użytkownika");
            return false;
        }
    }
    public boolean showInfo() {
        if(loggedIn.getRole()==Role.USER) {
            Vehicle h = vehicleRepository.getVehicle(loggedIn.getRentedVehicleId());
            System.out.println("Login użytkownika: " + loggedIn.getLogin());
            if (h == null) {
                System.out.println("Użytkownik nie wypożyczył pojazdu");
                return false;
            }
            System.out.println("ID wypożyczonego pojazdu: " + loggedIn.getRentedVehicleId());
            System.out.println("Marka pojazdu: " + h.getBrand());
            System.out.println("Model pojazdu: " + h.getModel());
            System.out.println("Rok produkcji: " + h.getYear());
            System.out.println("Cena: " + h.getPrice());
            return true;
        } else {
            System.out.println("Wyświetlenie danych to funkcja dla użytkowników.");
            return false;
        }
    }
    public boolean rentCar() {
        if(loggedIn.getRole()==Role.USER) {
            if(loggedIn.getRentedVehicleId()!=null) {
                System.out.println("Posiadasz już wypożyczony pojazd");
                return false;
            }
            System.out.println("Podaj id pojazdu");
            String id = scanner.nextLine();
            Vehicle h = vehicleRepository.getVehicle(id);
            if (h == null || h.isRented()) {
                System.out.println("Takiego pojazdu nie ma albo jest niedostępny");
                return false;
            }
            h.setRented(true);
            System.out.println("Wypożyczenie udane.");
            return true;
        } else {
            System.out.println("Tylko użytkownicy mogą wyporzyczać pojazdy");
            return false;
        }
    }
    public boolean returnCar() {
        if(loggedIn.getRole()==Role.USER) {
            System.out.println("Zwracanie pojazdu...");
            Vehicle h = vehicleRepository.getVehicle(loggedIn.getRentedVehicleId());
            if (h == null) {
                System.out.println("Taki pojazd nie istnieje");
                return false;
            }
            h.setRented(false);
            loggedIn.returnVehicle();
            System.out.println("Zwrot udany.");
            return true;
        } else {
            System.out.println("Tylko użytkownicy mogą wypożyczać pojazdy");
            return false;
        }
    }
    public boolean showAll() {
        if(loggedIn.getRole()==Role.ADMIN) {
            for(User u: userRepository.getUsers()) {
                Vehicle h = vehicleRepository.getVehicle(u.getRentedVehicleId());
                System.out.println("Login użytkownika: " + u.getLogin());
                if(h==null) {
                    System.out.println("Użytkownik nie ma wypożyczonego pojazdu.");
                } else {
                    System.out.println("ID wypożyczonego pojazdu: " + u.getRentedVehicleId());
                    System.out.println("Marka pojazdu: " + h.getBrand());
                    System.out.println("Model pojazdu: " + h.getModel());
                    System.out.println("Rok produkcji: " + h.getYear());
                    System.out.println("Cena: " + h.getPrice());
                }
            }
            return true;
        } else {
            System.out.println("Tylko administrator może wyświetlić listę użytkowników.");
            return false;
        }
    }
    public boolean addVehicle() {
        if(loggedIn.getRole()==Role.ADMIN) {
            String id;
            String marka;
            String model;
            Integer rok;
            Double cena;
            System.out.println("Samochód czy Motocykl?");
            String a = scanner.nextLine();
            if(a.equalsIgnoreCase("motocykl")) {
                System.out.println("Podaj ID motocyklu:");
                id = scanner.nextLine();
                System.out.println("Podaj marke motocyklu:");
                marka = scanner.nextLine();
                System.out.println("Podaj model motocyklu:");
                model = scanner.nextLine();
                System.out.println("Podaj rok produkcji motocyklu:");
                rok = Integer.parseInt(scanner.nextLine());
                System.out.println("Podaj cene motocyklu:");
                cena = Double.parseDouble(scanner.nextLine());
                System.out.println("Podaj kategorie motocyklu:");
                MotorcycleCategory k = MotorcycleCategory.valueOf(scanner.nextLine());
                vehicleRepository.add(new Motorcycle(id, marka, model, rok, cena, false, k));
                System.out.println("Motocykl pomyslnie dodany");
                return true;
            } else if(a.equalsIgnoreCase("samochod")) {
                System.out.println("Podaj ID samochodu:");
                id = scanner.nextLine();
                System.out.println("Podaj marke samochodu:");
                marka = scanner.nextLine();
                System.out.println("Podaj model samochodu:");
                model = scanner.nextLine();
                System.out.println("Podaj rok produkcji samochodu:");
                rok = Integer.parseInt(scanner.nextLine());
                System.out.println("Podaj cene samochodu:");
                cena = Double.parseDouble(scanner.nextLine());
                vehicleRepository.add(new Car(id, marka, model, rok, cena, false));
                System.out.println("Samochod pomyslnie dodany");
                return true;
            } else {
                System.out.println("nie ma takiego pojazdu");
                return false;
            }
        } else {
            System.out.println("Dodać pojazd do bazy może tylko administrator");
            return false;
        }
    }
    public boolean removeVehicle() {
        if(loggedIn.getRole()==Role.ADMIN) {
            System.out.println("Podaj ID pojazdu:");
            String id = scanner.nextLine();
            vehicleRepository.remove(id);
            System.out.println("Pojazd pomyslnie usunięty");
            return true;
        } else {
            System.out.println("Usunąć pojazd z bazy może tylko administrator");
            return false;
        }
    }
    public boolean showVehicles() {
        if(loggedIn.getRole()==Role.ADMIN) {
            for(Vehicle v: vehicleRepository.getVehicles()) {
                System.out.println("Id: "+v.getId());
                System.out.println("Model: "+v.getModel());
                System.out.println("Marka: "+v.getBrand());
                System.out.println("Cena: "+v.getPrice());
                System.out.println("Rok: "+v.getYear());
                if(v.isRented()) {
                    System.out.println("Wypożyczony");
                } else {
                    System.out.println("Nie wypożyczony");
                }
                if (v instanceof Motorcycle m) {
                    System.out.println(m.getKategoria());
                }
            }
            return true;
        } else {
            System.out.println("Sprawdzic liste pojazdow może tylko administrator");
            return false;
        }
    }

    public UI(Authentication auth, VehicleRepositoryImpl vehicleRepository) {
        this.auth = auth;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = auth.getRepo();
    }
}
