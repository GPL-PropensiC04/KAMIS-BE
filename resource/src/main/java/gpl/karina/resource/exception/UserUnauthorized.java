package gpl.karina.resource.exception;

public class UserUnauthorized extends RuntimeException {
    public UserUnauthorized(String message) {
        super(message);
    }
    
}
