package utils;

/**
 * this exception is thrown when there is no data found with the given conditions.
 */
public class NoSuchDataException extends Exception{
    public NoSuchDataException(String message){
        super(message);
    }
}
