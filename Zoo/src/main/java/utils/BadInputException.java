package utils;

/**
 * This exception is thrown when the input is not accurate.
 */
public class BadInputException extends Exception{
    /**
     * Constructor
     * @param message String
     */
    public BadInputException(String message){
        super(message);
    }
}
