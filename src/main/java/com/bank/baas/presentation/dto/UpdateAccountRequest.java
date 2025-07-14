package com.bank.baas.presentation.dto;

public record UpdateAccountRequest(
    String email,
    String currentPassword,
    String newPassword
) {
    @Override
    public String toString() {
        return "UpdateAccountRequest{" +
                "email='" + email + '\'' +
                ", currentPassword='[PROTECTED]'" +
                ", newPassword='[PROTECTED]'" +
                '}';
    }
}