package ConjurApi.Exceptions;

public class ConjurApiAuthenticateException extends Exception { 
    public ConjurApiAuthenticateException(String errorMessage) {
        super(errorMessage);
    }
}