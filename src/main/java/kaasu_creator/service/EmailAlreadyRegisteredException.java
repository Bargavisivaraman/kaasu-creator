package kaasu_creator.service;

/**
 * Thrown when registration is attempted with an email that already exists.
 * Using a specific exception type (instead of a raw RuntimeException) lets
 * callers and tests handle this case explicitly.
 */
public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException() {
        super("Email is already registered");
    }
}
