package repository.jdbcRepository;

import domain.Instructor;
import repository.InstructorRepository;

import javax.persistence.*;
import java.sql.*;
import java.util.List;

/**
 * JdbcInstructorRepository implements the interface InstructorRepository. The data is saved in the database.
 */
public class JdbcInstructorRepository implements InstructorRepository {
    /**
     * EntityManager - used for different operations in the database
     */
    private EntityManager manager;

    /**
     Constructor - constructs and initializes an JdbcInstructorRepository. <br>
     * Initially the list of Instructors is empty, then the method populateInstructors() is called.
     * @param persistenceName name of the used persistence
     */
    public JdbcInstructorRepository(String persistenceName) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceName);
        this.manager =  factory.createEntityManager();
        // this.populateInstructors();
    }

    /**
     * This method populates with Instructors the database.
     */
    private void populateInstructors(){
        Instructor instructor1 = new Instructor("i1","James", "Parker", "123456");
        Instructor instructor2 = new Instructor("i2","James", "John", "qwerty");
        Instructor instructor3 = new Instructor("i3", "Lucy", "Misterious", "abc123");
        Instructor instructor4 = new Instructor("i4", "Katy", "Gal", "123321");
        Instructor instructor5 = new Instructor("i5","Camila", "Pop", "password1");
        Instructor instructor6 = new Instructor("i6","Mircea", "Miron", "abcd1234");

        manager.getTransaction().begin();
        manager.persist(instructor1); manager.persist(instructor2); manager.persist(instructor3);
        manager.persist(instructor4); manager.persist(instructor5); manager.persist(instructor6);
        manager.getTransaction().commit();
    }

    /**
     * This method reads out from the database and returns the list of Instructors.
     * @return The list of Instructors
     */
    @Override
    public List<Instructor> getAllInstructors() {
        manager.getTransaction().begin();
        Query query = manager.createNativeQuery("select * from instructor", Instructor.class);
        List<Instructor> instructors = (List<Instructor>) query.getResultList();
        manager.getTransaction().commit();
        return instructors;
    }

    /**
     * This method adds an Instructor to the database. <br>
     * If there is already an Instructor in the repository with the same ID, the new Instructor won't be added.
     * @param instructor Instructor who will be added.
     */
    @Override
    public void add(Instructor instructor) {
        try{
            if (this.findByID(instructor.getID()) == null) {
                manager.getTransaction().begin();
                manager.persist(instructor);
                manager.getTransaction().commit();
            }
            else System.out.println("Es gibt schon eine Instruktor mit dieser ID");
        }catch (NullPointerException ignored) {}
    }

    /**
     * This method deletes an Instructor from the database.
     * @param id String - the ID of the Instructor who will be eliminated
     */
    @Override
    public void delete(String id) {
        Instructor instructor = this.findByID(id);
        manager.getTransaction().begin();
        manager.remove(instructor);
        manager.getTransaction().commit();
    }

    /**
     * This method updates an Instructor in the database. <br>
     * @param idInstructor String - the ID of the Instructor who will be updated
     * @param instructor the new Instructor who will appear instead of the old Instructor
     */
    @Override
    public void update(String idInstructor, Instructor instructor) {
        Instructor instr = this.findByID(idInstructor);
        if (instr != null)
        {
            manager.getTransaction().begin();
            instr.setFirstName(instructor.getFirstName());
            instr.setLastName(instructor.getLastName());
            instr.setPassword(instructor.getPassword());
            instr.setFinalSum(instructor.getFinalSum());
            instr.setAttractions(instructor.getAttractions());
            manager.getTransaction().commit();
        }
      }

    /**
     * This method returns the Instructor who has the ID given as a parameter. <br>
     * @param idInstructor String - the ID of the Instructor who is searched
     * @return the Instructor who has the ID given as a parameter or null if there is no Instruction with the given ID
     */
    @Override
    public Instructor findByID(String idInstructor) {
        try{
            manager.getTransaction().begin();
            Instructor instructor = manager.find(Instructor.class, idInstructor);
            manager.getTransaction().commit();
            return instructor;
        } catch (NoResultException e){
                return null;}
    }

    /**
     * This method returns the EntityManager.
     * @return EntityManager of the repository
     */
    public EntityManager getManager() {
        return manager;
    }
}
