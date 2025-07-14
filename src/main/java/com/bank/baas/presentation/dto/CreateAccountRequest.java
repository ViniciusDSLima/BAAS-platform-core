package com.bank.baas.presentation.dto;

public record CreateAccountRequest(
    String email,
    String cpf,
    String password
) {
    @Override
    public String toString() {
        return "CreateAccountRequest{" +
                "email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}