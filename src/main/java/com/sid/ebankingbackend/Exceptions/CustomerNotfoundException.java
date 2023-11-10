package com.sid.ebankingbackend.Exceptions;

public class CustomerNotfoundException extends Exception {
    public CustomerNotfoundException(String customerNotFound) {
        super(customerNotFound);
    }
}
