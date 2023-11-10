package com.sid.ebankingbackend.Exceptions;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String bankAccoutNotFound) {
        super(bankAccoutNotFound);
    }
}
