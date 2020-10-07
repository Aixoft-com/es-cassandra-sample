package com.aixoft.essample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UnexpectedEventVersionException extends Exception {
    public UnexpectedEventVersionException(String message) {
        super(message);
    }
}
