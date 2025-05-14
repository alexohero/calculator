package com.mx.raven.calculator.exceptions;

public class InvalidObjectException extends RuntimeException{

    public InvalidObjectException(){}

    public InvalidObjectException(String message){
        super(message);
    }

    public InvalidObjectException(String message, Throwable cause) {
        super(message, cause);
    }

}
