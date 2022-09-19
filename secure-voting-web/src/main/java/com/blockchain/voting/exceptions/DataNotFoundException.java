package com.blockchain.voting.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created on 07 Sep 2022
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(String message) {
        super(message);
        this.setStackTrace(new StackTraceElement[0]);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.setStackTrace(new StackTraceElement[0]);
    }
}

