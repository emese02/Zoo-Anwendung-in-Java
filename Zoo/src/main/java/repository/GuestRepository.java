package repository;
import domain.Guest;
import java.util.List;

/**
 * GuestRepository extending ICrudRepository with getAllGuests method.
 */
public interface GuestRepository extends ICrudRepository<String, Guest>{
    /**
     * This method returns the list of Guests.
     * @return The list of Guests
     */
    List<Guest> getAllGuests();
}
