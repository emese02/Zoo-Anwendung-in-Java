package repository;
import domain.Guest;
import domain.Instructor;

import java.util.List;

/**
 * InstructorRepository extending ICrudRepository with getAllInstructors method.
 */
public interface InstructorRepository extends ICrudRepository<String, Instructor>{
    /**
     * This method returns the list of Instructors.
     * @return The list of Instructors
     */
    List<Instructor> getAllInstructors();
}
