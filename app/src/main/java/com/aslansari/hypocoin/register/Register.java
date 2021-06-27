package com.aslansari.hypocoin.register;

import com.aslansari.hypocoin.register.exception.PasswordMismatchException;

public class Register {

    public boolean validateUsername(String userName) throws IllegalArgumentException {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("username cannot be empty");
        }
        return true;
    }

    public boolean validatePassword(String password, String passwordRepeat) throws IllegalArgumentException, PasswordMismatchException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password field cannot be empty");
        }
        if (passwordRepeat == null || passwordRepeat.isEmpty()) {
            throw new IllegalArgumentException("password field cannot be empty");
        }
        if (!password.equals(passwordRepeat)) {
            throw new PasswordMismatchException("password fields does not match");
        }
        return true;
    }
}
