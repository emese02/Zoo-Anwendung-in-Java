package repository.jdbcRepository;

import domain.Attraction;
import domain.Instructor;
import domain.Weekday;
import repository.AttractionRepository;
import repository.InstructorRepository;

import javax.persistence.*;
import java.sql.Connection;
import java.util.List;

/**
 * JdbcAttractionRepository implements the interface AttractionRepository. The data is saved in database.
 */
public class JdbcAttractionRepository implements AttractionRepository {
    /**
     * InstructorRepository from where the Instructor of the Attractions can be selected.
     */
    private final InstructorRepository instructorRepository;
    /**
     * EntityManager - used for different operations with database
     */
    private final EntityManager manager;

    /**
     * Constructor - constructs and initializes an JdbcAttractionRepository. <br>
     * Initially the List of Instructors is empty, then the method populateAttractions() is called.
     * @param instructorRepository InstructorRepository from where the Instructor of the Attractions can be selected.
     */
    public JdbcAttractionRepository(JdbcInstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
        this.manager = instructorRepository.getManager();
        // this.populateAttractions();
    }

    /**
     * This method populates with Attractions the database. <br>
     * When the Instructor is added to the Attraction, the Attraction appears in the Attraction-list of the Instructor as well.
     */
    private void populateAttractions(){
        List<Instructor> instructors = this.instructorRepository.getAllInstructors();
        Instructor instructor1 = instructors.get(0);
        Instructor instructor2 = instructors.get(1);
        Instructor instructor3 = instructors.get(2);
        Instructor instructor4 = instructors.get(3);
        Instructor instructor5 = instructors.get(4);
        Instructor instructor6 = instructors.get(5);

        Attraction attraction1 = new Attraction("Zoo time",100, instructor1,180.99, "A456", Weekday.MONDAY);
        Attraction attraction2 = new Attraction("Elephantastic",200, instructor2,99.99,"B456", Weekday.TUESDAY);
        Attraction attraction3 = new Attraction("Fluffy Animals",10, instructor3,55.00,"A456", Weekday.WEDNESDAY);
        Attraction attraction4 = new Attraction("Angry Panda",17, instructor4,70.00,"X588", Weekday.THURSDAY);
        Attraction attraction5 = new Attraction("White Lion",8, instructor5,280.00,"F444", Weekday.FRIDAY);
        Attraction attraction6 = new Attraction("White Lion",80, instructor2,170.89,"T545", Weekday.SATURDAY);
        Attraction attraction7 = new Attraction("White Lion",55, instructor4,60.50,"RQ67", Weekday.SUNDAY);
        Attraction attraction8 = new Attraction("VIP zoo show",10, instructor6,300.87,"BRT60", Weekday.WEDNESDAY);

        instructor1.addAttraction(attraction1);
        instructor2.addAttraction(attraction2);
        instructor3.addAttraction(attraction3);
        instructor4.addAttraction(attraction4);
        instructor5.addAttraction(attraction5);
        instructor2.addAttraction(attraction6);
        instructor4.addAttraction(attraction7);
        instructor6.addAttraction(attraction8);

        // update the attractionlist of instructors in database too
        instructorRepository.update(instructor1.getID(), instructor1);
        instructorRepository.update(instructor2.getID(), instructor2);
        instructorRepository.update(instructor3.getID(), instructor3);
        instructorRepository.update(instructor4.getID(), instructor4);
        instructorRepository.update(instructor5.getID(), instructor5);
        instructorRepository.update(instructor6.getID(), instructor6);
    }

    /**
     * This method reads out from the database and returns the list of Attractions.
     * @return The list of Attractions
     */
    @Override
    public List<Attraction> getAllAttractions() {
        manager.getTransaction().begin();
        Query query = manager.createNativeQuery("SELECT * FROM attraction", Attraction.class);
        List<Attraction> attractions = (List<Attraction>) query.getResultList();
        manager.getTransaction().commit();
        return attractions;
    }

    /**
     * This method adds an Attraction to the database. <br>
     * If there is already an Attraction in the repository with the same ID, the new Attraction won't be added.
     * @param attraction Attraction which will be added.
     */
    @Override
    public void add(Attraction attraction) {
        try {
            if (this.findByID(attraction.getID()) == null) {
                manager.getTransaction().begin();
                manager.persist(attraction);
                manager.getTransaction().commit();
            }
            else System.out.println("Eine Attraktion mit dieser ID existiert schon");
        } catch (NullPointerException ignored){}
    }

    /**
     * This method deletes an Attraction from the databases.
     * @param id String - the ID of the Attraction which will be eliminated
     */
    @Override
    public void delete(String id){
        Attraction attraction = this.findByID(id);
        manager.getTransaction().begin();
        manager.remove(attraction);
        manager.getTransaction().commit();
    }

    /**
     * This method updates an Attraction in the database. <br>
     * @param idAttraction String - the ID of the Attraction which will be updated
     * @param attraction the new Attraction who will appear instead of the old Attraction
     */
    @Override
    public void update(String idAttraction, Attraction attraction){
        Attraction attr = this.findByID(idAttraction);
        if (attr != null)
        {
            manager.getTransaction().begin();
            attr.name = attraction.name;
            attr.guestList = attraction.guestList;
            attr.day = attraction.day;
            attr.price = attraction.price;
            attr.location = attraction.location;
            attr.setInstructor(attraction.getInstructor());
            manager.getTransaction().commit();
        }
    }

    /**
     * This method returns the Attraction who has the ID given as parameter. <br>
     * @param idAttraction String - the ID of the Attraction who is searched
     * @return the Attraction who has the ID given as parameter or null if there is no Attraction with the given ID
     */
    @Override
    public Attraction findByID(String idAttraction) {
        try{
            manager.getTransaction().begin();
            Attraction attraction = manager.find(Attraction.class, idAttraction);
            manager.getTransaction().commit();
            return attraction;
        } catch (NoResultException e){
            return null;}
    }

    /**
     * This method returns the EntityManager.
     * @return EntityManager of the repository
     */
    public EntityManager getManager(){
        return manager;
    }

    /**
     * This method returns the InstructorRepository from where were the Instructors of the Attractions were selected.
     * @return Instructor
     */
    public InstructorRepository getInstructorRepository(){
        return instructorRepository;
    }

}
