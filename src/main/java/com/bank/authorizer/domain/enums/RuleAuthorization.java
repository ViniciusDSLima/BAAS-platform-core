package com.bank.authorizer.domain.enums;

public enum RuleAuthorization {
    CARD_NOT_FOUND("CARTAO_INEXISTENTE"),
    WRONG_PASSWORD("SENHA_INVALIDA"),
    INSUFFICIENT_BALANCE("SALDO_INSUFICIENTE");

    private final String message;

    RuleAuthorization(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
