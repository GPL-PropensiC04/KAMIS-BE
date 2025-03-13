package gpl.karina.purchase.exception;

public class UserUnauthorized extends RuntimeException {
    public UserUnauthorized(String message) {
        super(message);
    }
    
}
