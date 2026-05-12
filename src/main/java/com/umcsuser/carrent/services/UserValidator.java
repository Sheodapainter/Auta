package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;

public class UserValidator {
    private void requireNonBlank(String value, String message) {
        if(value == null | value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
    private void requireRole(Role role) {
        if(role == null) {
            throw new IllegalArgumentException("Rola jest wymagana");
        }
    }
    private void validateFields(User user) {
        requireNonBlank(user.getLogin(), "Login jest wymagany");
        requireNonBlank(user.getPasswordHash(), "Szyfr hasła jest wymagany");
        requireRole(user.getRole());
    }
    public UserValidator() {}
    public void validateUser(User user) {
        if (user == null) throw new IllegalArgumentException("Użytkownik nie może być null.");
        validateFields(user);
    }
}
