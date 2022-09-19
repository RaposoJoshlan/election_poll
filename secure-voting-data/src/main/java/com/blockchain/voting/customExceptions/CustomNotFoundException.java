package com.blockchain.voting.customExceptions;

/**
 * Created on 07 Sep 2022
 */


public class CustomNotFoundException extends IndexOutOfBoundsException {

    public CustomNotFoundException() {
        super();
    }

    public CustomNotFoundException(String message) {
        super(message);
        this.setStackTrace(new StackTraceElement[0]);
    }
}
