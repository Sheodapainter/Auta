package com.umcsuser.carrent;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.impl.RentalJsonRepository;
import com.umcsuser.carrent.repositories.impl.UserJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleJsonRepository;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args){
        UserJsonRepository userRepository = new UserJsonRepository();
        VehicleJsonRepository vehicleRepository = new VehicleJsonRepository();
        RentalJsonRepository rentalRepository = new RentalJsonRepository();
        AuthService authService = new AuthService(userRepository);
        VehicleCategoryConfigRepository configRepository = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(configService);
        RentalValidator rentalValidator = new RentalValidator();
        RentalService rentalService = new RentalService(rentalRepository, rentalValidator);
        VehicleService vehicleService = new VehicleService(vehicleValidator, vehicleRepository, rentalService);
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserService(userRepository, userValidator, rentalService);
        UI ui = new UI(userService, rentalService, authService, configService, vehicleService);
        ui.start();
    }
}