package juja.microservices.users.exceptions;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */
public class UserException extends RuntimeException{
    public UserException(String message) {
        super(message);
    }
}