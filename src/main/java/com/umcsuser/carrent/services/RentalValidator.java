package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RentalValidator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
    private void requireNonBlank(String value, String message) {
        if(value == null | value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
    private void dateFormatCheck(String string) {
        if(string!=null) {
            try {
                LocalDateTime.parse(string, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Niepoprawny format daty " + e);
            }
        }
    }
    private void validateFields(Rental rental) {
        requireNonBlank(rental.getVehicleId(), "id pojazdu jest wymagane");
        requireNonBlank(rental.getUserId(), "id uzytkownika jest wymagane");
        dateFormatCheck(rental.getRentDateTime());
        dateFormatCheck(rental.getReturnDateTime());
    }

    public void validateRental(Rental rental) {
        if (rental == null) throw new IllegalArgumentException("Rental nie może być null.");
        validateFields(rental);
    }
}
