package domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the class Person and models Instructors, who hold Attractions (shows) in the zoo.
 */
@Entity
public class Instructor extends Person{

    /**
     * List of Attractions which are held by the Instructor. <br>
     * One Instructor can hold more than one Attraction.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_id")
    private List<Attraction> attractions;

    /**
     * Constructor - constructs and initializes an Instructor. <br>
     * Also uses the constructor from the class Person(super-class) for the attributes: username, firstName, lastName, password.
     * @param id Instructor's id
     * @param firstName Instructor's firstname
     * @param lastName Instructor's lastname
     * @param password Instructor's passwords
     * @param attractionsOfInstructor list of Attractions which are held by the Instructor
     * @param sum income from the held Attractions (depends on the number of signed up Guests)
     */
    public Instructor(String id, String firstName, String lastName, String password, List<Attraction> attractionsOfInstructor, double sum) {
        super(id, firstName, lastName, password, sum);
        this.attractions = attractionsOfInstructor;
        calculateSum();
    }

    /**
     * Constructor - constructs and initializes an Instructor without having previously assigned Attractions. <br>
     * List of Attractions is initially an empty list. <br>
     * Also uses the constructor from the class Person (super-class) for the attributes: username, firstName, lastName, password.
     * @param id Instructor's id
     * @param firstName Instructor's firstname
     * @param lastName Instructor's lastname
     * @param password Instructor's password
     */
    public Instructor(String id, String firstName, String lastName, String password) {
        super(id, firstName, lastName, password);
        this.attractions = new ArrayList<Attraction>();
    }

    /**
     * Empty constructor.
     */
    public Instructor(){}

    /**
     * This method returns the list of Attractions held by the Instructor.
     * @return The list of Attractions held by the Instructor.
     */
    public List<Attraction> getAttractions() {
        return attractions;
    }

    /**
     * This method sets the list of the Attractions equal with the list given as parameter.
     * @param attractions List of Attractions
     */
    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    /**
     * This method adds an Attraction to the Instructor's list of Attractions.
     * @param attraction Attraction which will be held by the Instructor
     */
    public void addAttraction(Attraction attraction){
        this.attractions.add(attraction);
        calculateSum();
    }

    /**
     * This method removes the Attraction given as parameter from the Instructor's list of Attractions.
     * @param attraction Attraction which won't be held anymore by the Instructor
     */
    public void removeAttraction(Attraction attraction){
        this.attractions.remove(attraction);
        calculateSum();
    }

    /**
     * This method returns a String containing succinct information about the Instructor.
     * @return A String concatenated from the first name and the last name of the Instructor
     */
    @Override
    String getData() {
        return "Name: " + firstName + ' ' + lastName;
    }

    /**
     * This method calculates and updates the income of the Instructor for the Attractions which he/she holds. <br>
     * The method adds the income from each Attraction.
     */
    @Override
    public void calculateSum() {
        this.finalSum = 0;
        long youngNr, oldNr;
        for (Attraction attr: this.attractions){
           youngNr = attr.guestList
                    .stream()
                    .filter(g -> g.getAge() < 18)
                    .count();

           oldNr = attr.guestList
                   .stream()
                   .filter(g -> g.getAge() > 60)
                   .count();

           finalSum += attr.price * (attr.getNrOfGuests() - youngNr * 0.5 - oldNr * 0.2);
        }
    }

    /**
     * This method converts the Instructor object to a String.
     * @return A String containing the ID, name and sum acquired from the held Attractions. <br>
     * Additionally, information is included about the hold Attractions:
     * <ul>
     *     <li>Attraction name</li>
     *     <li>day of Attraction</li>
     *     <li>location</li>
     * </ul>
     */
    @Override
    public String toString() {
        String attractions_name = "";
        for (Attraction a: this.attractions)
            attractions_name = attractions_name + "(" + a.name + " " + a.day + ", " + a.location + "), ";

        // remove "," from the end
        if (attractions_name.length() > 0)
            attractions_name = attractions_name.substring(0, attractions_name.length() - 2);
        return  "  ID=" + ID  +
                "  firstName=" + firstName +
                "  lastName=" + lastName +
                "  finalSumFromGuests=" + finalSum +
                "  hält: " + attractions_name +
                '\n';
    }
}
