package utils;

/**
 * This exception is thrown when there are no more available tickets for an Attraction.
  */
public class NoMoreAvailableTicketsException extends Exception {
    public NoMoreAvailableTicketsException(String message){
        super(message);
    }
}
