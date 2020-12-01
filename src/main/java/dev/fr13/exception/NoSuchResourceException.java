package dev.fr13.exception;

public class NoSuchResourceException extends RuntimeException {

    public NoSuchResourceException(String msg) {
        super(msg);
    }
}
