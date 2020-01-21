package ConjurApi.Exceptions;

public class InvalidHttpStatusCodeException extends Exception { 
    public InvalidHttpStatusCodeException(String errorMessage) {
        super(errorMessage);
    }
}