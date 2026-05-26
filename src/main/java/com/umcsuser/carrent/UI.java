package com.umcsuser.carrent;

import com.umcsuser.carrent.models.*;
import com.umcsuser.carrent.services.*;

import java.util.*;

import static com.umcsuser.carrent.models.Role.ADMIN;
import static com.umcsuser.carrent.models.Role.USER;

public class UI {
    private User loggedIn = null;
    private final VehicleCategoryConfigService configService;
    private final VehicleServiceInterface vehicleService;
    private final UserServiceInterface userService;
    private final RentalServiceInterface rentalService;
    private final AuthServiceInterface authService;
    private final Scanner scanner = new Scanner(System.in);
    public User currentUser() {
        return loggedIn;
    }
    public UI(UserServiceInterface userService, RentalServiceInterface rentalService, AuthServiceInterface authService, VehicleCategoryConfigService configService, VehicleServiceInterface vehicleService) {
        this.authService = authService;
        this.userService = userService;
        this.rentalService = rentalService;
        this.configService = configService;
        this.vehicleService = vehicleService;
    }
    public boolean login() {
        System.out.println("Podaj login użytkownika:");
        String log = scanner.nextLine();
        System.out.println("Podaj hasło:");
        String pass = scanner.nextLine();
        Optional<User> cur = authService.login(log, pass);
        if(cur.isEmpty()) {
            System.out.println("Niepoprawne dane logowania");
            return false;
        } else {
            loggedIn = cur.get();
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
            System.out.println("Podaj hasło: ");
            String password = scanner.nextLine();
            Boolean cur = authService.register(username, password);
            if(cur) {
                System.out.println("Użytkownik zarejestrowany pomyślnie");
            } else {
                System.out.println("Nazwa użytkownika zajęta");
            }
            return cur;
        } else {
            System.out.println("Posiadasz już dane użytkownika");
            return false;
        }
    }
    public boolean deleteUser() {
        if(loggedIn.getRole()==Role.ADMIN) {
            System.out.println("Podaj nazwę użytkownika: ");
            String username = scanner.nextLine();
            String uid = null;
            List<User> users = userService.findAll();
            for(User u: users) {
                if(Objects.equals(u.getLogin(), username)) {
                    uid=u.getId();
                    break;
                }
            }
            try {
                userService.deleteUser(uid, loggedIn.getId());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return true;
        }
        else {
            System.out.println("Usunąć użytkownika może tylko administrator");
            return false;
        }
    }
    public boolean rentCar() {
        if(loggedIn.getRole()==Role.USER) {
            List<Rental> rentals = rentalService.findAllRentals();
            for(Rental r: rentals) {
                if(Objects.equals(r.getUserId(), loggedIn.getId())) {
                    if(r.getReturnDateTime()==null) {
                        System.out.println("Posiadasz już wypożyczony pojazd");
                        return false;
                    }
                }
            }
            System.out.println("Podaj id pojazdu");
            String id = scanner.nextLine();
            if(rentalService.vehicleHasActiveRental(id)) {
                System.out.println("Pojazd jest juz wypozyczony");
                return false;
            }
            for(Vehicle v: vehicleService.findAllVehicles()) {
                if (Objects.equals(v.getId(), id)) {
                    rentalService.rentVehicle(loggedIn.getId(), id);
                    System.out.println("Wypożyczenie udane.");
                    return true;
                }
            }
            System.out.println("Takiego pojazdu nie ma albo jest niedostępny");
            return false;
        } else {
            System.out.println("Tylko użytkownicy mogą wyporzyczać pojazdy");
            return false;
        }
    }
    public boolean returnCar() {
        if(loggedIn.getRole()==Role.USER) {
            Optional<Rental> rental = rentalService.findActiveRentalByUserId(loggedIn.getId());
            if(rental.isPresent()) {
                System.out.println("Znaleziono wypozyczony pojazd. Zwracanie...");
                rentalService.returnVehicle(loggedIn.getId());
                System.out.println("Zwrot pomyślny.");
                return true;
            }
            System.out.println("Użytkownik nie posiada wypożyczonego pojazdu");
            return false;
        } else {
            System.out.println("Tylko użytkownicy mogą wypożyczać pojazdy");
            return false;
        }
    }
    public boolean showAll() {
        if(loggedIn.getRole()==Role.ADMIN) {
            for(Vehicle v: vehicleService.findAllVehicles()) {
                System.out.println(v);
            }
            return true;
        } else {
            for(Vehicle v: vehicleService.findAvailableVehicles()) {
                System.out.println(v);
            }
            return true;
        }
    }
    public boolean showUsers() {
        if(loggedIn.getRole()==Role.ADMIN) {
            List<User> users = userService.findAll();
            for(User u: users) {
                Optional<Rental> r=rentalService.findActiveRentalByUserId(u.getId());
                System.out.println("Użytkownik: "+u.getLogin());
                if(r.isEmpty()) {
                    System.out.println("Nie posiada wypożyczonego pojazdu.");
                } else {
                    System.out.println(r.get().getVehicle());
                }
            }
            return true;
        } else {
            System.out.println("Wyświetlić dane użytkowników może tylko administrator");
            return false;
        }
    }
    public boolean showSelf() {
        if(loggedIn.getRole()==Role.USER) {
            System.out.println("Użytkownik "+loggedIn.getLogin());
            Optional<Rental> userRental = rentalService.findActiveRentalByUserId(loggedIn.getId());
            if(userRental.isEmpty()) {
                System.out.println("Nie posiada wypożyczonego pojazdu.");
            } else {
                System.out.println(userRental.get().getVehicle());
            }
            return true;
        } else {
            System.out.println("Tylko użytkownicy mogą wyświetlać dane swojego pojazdu.");
            return false;
        }
    }
    public boolean addVehicle() {
        if(loggedIn.getRole()==Role.ADMIN) {
            try {
                String marka;
                String model;
                Integer rok;
                String tablica;
                Double cena;
                System.out.println("\nDostępne kategorie:");
                configService.findAllCategories().forEach(c -> System.out.println("- " + c.getCategory()));
                System.out.print("\nPodaj kategorię: ");
                VehicleCategoryConfig config = configService.getByCategory(scanner.nextLine().trim());
                System.out.println("\nPodaj marke pojazdu:");
                marka = scanner.nextLine();
                System.out.println("\nPodaj model pojazdu:");
                model = scanner.nextLine();
                System.out.println("\nPodaj rok produkcji pojazdu:");
                rok = Integer.parseInt(scanner.nextLine());
                System.out.println("\nPodaj numer tablicy pojazdu:");
                tablica = scanner.nextLine();
                System.out.println("\nPodaj cene pojazdu:");
                cena = Double.parseDouble(scanner.nextLine());
                Vehicle vehicle = Vehicle.builder()
                        .category(config.getCategory())
                        .brand(marka)
                        .model(model)
                        .year(rok)
                        .plate(tablica)
                        .price(cena)
                        .build();
                config.getAttributes().forEach((attrName, attrType) -> {
                    System.out.print("Podaj wartość atrybutu " + attrName + " (" + attrType + "): ");
                    String rawValue = scanner.nextLine().trim();

                    Object value = (Object) switch (attrType.toLowerCase()) {
                        case "string" -> rawValue;
                        case "integer" -> Integer.parseInt(rawValue);
                        case "number" -> Double.parseDouble(rawValue);
                        case "boolean" -> Boolean.parseBoolean(rawValue);
                        default -> throw new IllegalArgumentException("Nieobsługiwany typ: " + attrType);
                    };
                    vehicle.addAttribute(attrName, value);
                });
                vehicleService.addVehicle(vehicle);
                System.out.println("Pojazd pomyslnie dodany:");
                return true;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
            }
        } else {
            System.out.println("Dodać pojazd do bazy może tylko administrator");
        }
        return false;
    }
    public boolean removeVehicle() {
        if(loggedIn.getRole()==Role.ADMIN) {
            System.out.println("Podaj ID pojazdu:");
            String id = scanner.nextLine();
            try {
                vehicleService.removeVehicle(id);
                System.out.println("Pojazd pomyślnie usunięty.");
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return true;
        } else {
            System.out.println("Usunąć pojazd z bazy może tylko administrator");
            return false;
        }
    }
    public void start() {
        String command;
        while(true) {
            if (currentUser()==null) {
                System.out.println("Nie zalogowano. Dostępne komendy:");
                System.out.println("\"login\" aby się zalogować");
                System.out.println("\"register\" aby się zaresestrować");
                System.out.println("\"exit\" aby wyjść z programu");
                System.out.println("\n");
                command = scanner.nextLine();
                if(command.equalsIgnoreCase("login")) {
                    login();
                } else if (command.equalsIgnoreCase("register")) {
                    register();
                } else if (command.equalsIgnoreCase("exit")) {
                    break;
                } else {
                    System.out.println("Niepoprawna komenda. Spróbuj ponownie.");
                }
            } else if (currentUser().getRole()==ADMIN) {
                System.out.println("\n");
                System.out.println("Zalogowano jako "+currentUser().getLogin()+", administrator.");
                System.out.println("Dostępne komendy:");
                System.out.println("\"show\" aby pokazać listę pojazdów");
                System.out.println("\"showUsers\" aby pokazać listę użytkowników i ich pojazdy");
                System.out.println("\"add\" aby dodać pojazd");
                System.out.println("\"removeVehicle\" aby usunąć pojazd");
                System.out.println("\"deleteUser\" aby usunąć użytkownika");
                System.out.println("\"logout\" aby się wylogować");
                command = scanner.nextLine();
                if(command.equalsIgnoreCase("show")) {
                    showAll();
                } else if(command.equalsIgnoreCase("showusers")) {
                    showUsers();
                } else if(command.equalsIgnoreCase("add")) {
                    addVehicle();
                } else if(command.equalsIgnoreCase("removevehicle")) {
                    removeVehicle();
                } else if(command.equalsIgnoreCase("deleteuser")) {
                    deleteUser();
                } else if(command.equalsIgnoreCase("logout")) {
                    logout();
                } else {
                    System.out.println("Niepoprawna komenda. Spróbuj ponownie.");
                }
            } else if (currentUser().getRole()==USER) {
                System.out.println("\n");
                System.out.println("Zalogowano jako "+currentUser().getLogin()+", użytkownik.");
                System.out.println("Dostępne komendy:");
                System.out.println("\"show\" aby pokazać listę dostępnych pojazdów");
                System.out.println("\"showSelf\" aby wyswietlic aktualnie wypozyczony pojazd");
                System.out.println("\"rent\" aby wynająć pojazd");
                System.out.println("\"return\" aby zwrócić pojazd");
                System.out.println("\"logout\" aby się wylogować");
                command = scanner.nextLine();
                if(command.equalsIgnoreCase("show")) {
                    showAll();
                } else if(command.equalsIgnoreCase("showself")) {
                    showSelf();
                } else if(command.equalsIgnoreCase("rent")) {
                    rentCar();
                } else if(command.equalsIgnoreCase("return")) {
                    returnCar();
                } else if(command.equalsIgnoreCase("logout")) {
                    logout();
                } else {
                    System.out.println("Niepoprawna komenda. Spróbuj ponownie.");
                }
            } else {
                System.out.println("Błąd użytkownika!");
                break;
            }
        }
    }
}
//1. Dodanie pojazdu nie działa
//2. Usuwanie user/vehicle wypozyczone wywala program: Exception in thread "main" java.lang.IllegalStateException: org.hibernate.resource.jdbc.internal.LogicalConnectionManagedImpl@43ab0659 is closed
