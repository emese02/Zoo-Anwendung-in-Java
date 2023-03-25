package repository.memoryRepo;

import domain.Attraction;
import domain.Instructor;
import repository.InstructorRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * InMemoryInstructorRepository implements the interface InstructorRepository. The data is saved in memory.
 */
public class InMemoryInstructorRepository implements InstructorRepository {
    /**
     * List of Instructors
     */
    private final List<Instructor> allInstructors;

    /**
     Constructor - constructs and initializes an InMemoryInstructorRepository. <br>
     * Initially the List of Instructors is empty, then the method populateInstructors() is called.
     */
    public InMemoryInstructorRepository() {
        this.allInstructors = new ArrayList<>();
        this.populateInstructors();
    }

    /**
     * This method populates with data the list of Instructors with the help of add() method.
     */
    private void populateInstructors() {
        Instructor instructor1 = new Instructor("i1","James", "Parker", "123456");
        Instructor instructor2 = new Instructor("i2","James", "John", "qwerty");
        Instructor instructor3 = new Instructor("i3", "Lucy", "Misterious", "abc123");
        Instructor instructor4 = new Instructor("i4", "Katy", "Gal", "123321");
        Instructor instructor5 = new Instructor("i5","Camila", "Pop", "password1");
        Instructor instructor6 = new Instructor("i6","Mircea", "Miron", "abcd1234");

        this.add(instructor1);
        this.add(instructor2);
        this.add(instructor3);
        this.add(instructor4);
        this.add(instructor5);
        this.add(instructor6);
    }

    /**
     * This method returns the list of Instructors.
     * @return The list of Instructors
     */
    @Override
    public List<Instructor> getAllInstructors() {
        return allInstructors;
    }

    /**
     * This method adds an Instructor to the list of Instructors. <br>
     * If there is already an Instructor in the repository with the same ID, the new Instructor won't be added.
     * @param instructor Instructor who will be added.
     */
    @Override
    public void add(Instructor instructor) {
        try{
            for (Instructor instr: this.allInstructors){
                if (instr.getID().equals(instructor.getID())){
                    System.out.println("Es gibt schon eine Instruktor mit dieser ID");
                    return;
                }
            }
            this.allInstructors.add(instructor);
        }catch (NullPointerException ignored) {}

    }

    /**
     * This method deletes an Instructor from the list of Instructors.
     * @param id String - the ID of the Instructor who will be eliminated
     */
    @Override
    public void delete(String id) {
        Instructor instructor = this.findByID(id);
        this.allInstructors.remove(instructor);
    }

    /**
     * This method updates an Instructor from the list of Instructors. <br>
     * @param id String - the ID of the Instructor who will be updated
     * @param instructor the new Instructor who will appear instead of the old Instructor
     */
    @Override
    public void update(String id, Instructor instructor) {
        Instructor instr = this.findByID(id);
        int position = this.allInstructors.indexOf(instr);
        this.allInstructors.set(position, instructor);
    }

    /**
     * This method returns the Instructor who has the ID given as a parameter. <br>
     * @param id String - the ID of the Instructor who is searched
     * @return the Instructor who has the ID given as a parameter or null if there is no Instruction with the given ID
     */
    @Override
    public Instructor findByID(String id) {
        for (Instructor instructor: this.allInstructors){
            if(instructor.getID().equals(id))
                return instructor;
        }
        return null;
    }

}
