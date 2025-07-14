package com.bank.baas.presentation.dto;

public class UserRegistrationRequest {
    private String email;
    private String cpf;
    private String password;

    public UserRegistrationRequest() {
    }

    public UserRegistrationRequest(String email, String cpf, String password) {
        this.email = email;
        this.cpf = cpf;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}