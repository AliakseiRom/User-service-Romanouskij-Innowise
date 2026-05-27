package com.innowise.userservice.exceptions;

public class PaymentCardLimitExceededException extends CommonException {
    public PaymentCardLimitExceededException(String message) {
        super(message);
    }
}
