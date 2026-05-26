package com.umcsuser.carrent;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args){
        if(args.length > 0 && args[0].equals("jdbc")) {
            UserJdbcRepository userRepository = new UserJdbcRepository();
            VehicleJdbcRepository vehicleRepository = new VehicleJdbcRepository();
            RentalJdbcRepository rentalRepository = new RentalJdbcRepository();
            AuthService authService = new AuthService(userRepository);
            VehicleCategoryConfigRepository configRepository = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepository);
            VehicleValidator vehicleValidator = new VehicleValidator(configService);
            RentalService rentalService = new RentalService(rentalRepository, userRepository, vehicleRepository);
            VehicleService vehicleService = new VehicleService(vehicleValidator, vehicleRepository, rentalRepository);
            UserService userService = new UserService(userRepository, rentalRepository);
            UI ui = new UI(userService, rentalService, authService, configService, vehicleService);
            ui.start();
        } else if(args.length > 0 && args[0].equals("hibernate")) {
            UserHibernateRepository userRepository = new UserHibernateRepository();
            VehicleHibernateRepository vehicleRepository = new VehicleHibernateRepository();
            RentalHibernateRepository rentalRepository = new RentalHibernateRepository();
            AuthHibernateService authService = new AuthHibernateService(userRepository);
            VehicleCategoryConfigRepository configRepository = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepository);
            VehicleValidator vehicleValidator = new VehicleValidator(configService);
            RentalHibernateService rentalService = new RentalHibernateService(rentalRepository, vehicleRepository, userRepository);
            VehicleHibernateService vehicleService = new VehicleHibernateService(vehicleValidator, vehicleRepository, rentalRepository);
            UserHibernateService userService = new UserHibernateService(userRepository, rentalRepository);
            UI ui = new UI(userService, rentalService, authService, configService, vehicleService);
            ui.start();
        } else {
            UserJsonRepository userRepository = new UserJsonRepository();
            VehicleJsonRepository vehicleRepository = new VehicleJsonRepository();
            RentalJsonRepository rentalRepository = new RentalJsonRepository();
            AuthService authService = new AuthService(userRepository);
            VehicleCategoryConfigRepository configRepository = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepository);
            VehicleValidator vehicleValidator = new VehicleValidator(configService);
            RentalService rentalService = new RentalService(rentalRepository, userRepository, vehicleRepository);
            VehicleService vehicleService = new VehicleService(vehicleValidator, vehicleRepository, rentalRepository);
            UserService userService = new UserService(userRepository, rentalRepository);
            UI ui = new UI(userService, rentalService, authService, configService, vehicleService);
            ui.start();
        }
    }
}