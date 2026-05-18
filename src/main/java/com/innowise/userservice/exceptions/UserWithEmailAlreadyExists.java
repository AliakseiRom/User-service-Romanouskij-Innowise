package com.innowise.userservice.exceptions;

public class UserWithEmailAlreadyExists extends CommonException {
    public UserWithEmailAlreadyExists(String message) {
        super(message);
    }
}
