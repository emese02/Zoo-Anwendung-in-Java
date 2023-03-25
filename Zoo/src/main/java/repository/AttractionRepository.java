package repository;

import domain.Attraction;

import java.util.List;

/**
 * AttractionRepository extending ICrudRepository with getAllAttractions method.
 */
public interface AttractionRepository extends ICrudRepository<String, Attraction>{
    /**
     * This method returns the list of Attractions.
     * @return The list of Attractions
     */
    List<Attraction> getAllAttractions();
}
