package com.aslansari.hypocoin.register.dto;

public class RegisterInput {

    private final String username;
    private final String password;
    private final String passwordRepeat;

    public RegisterInput(String username, String password, String passwordRepeat) {
        this.username = username;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }
}
