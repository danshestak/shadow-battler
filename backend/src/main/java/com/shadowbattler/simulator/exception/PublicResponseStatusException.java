package com.shadowbattler.simulator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PublicResponseStatusException extends ResponseStatusException {
    public PublicResponseStatusException(HttpStatus status, String message) {
        super(status, message);
    }
}