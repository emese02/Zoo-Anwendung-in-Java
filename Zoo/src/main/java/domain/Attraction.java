package domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class models Attractions of the zoo.
 */
@Entity
public class Attraction implements Comparable<Attraction>{
    /**
     * Unique identification of the Attraction.
     */
    @Id
    private String ID;

    /**
     * Name of the Attraction.
     */
    public String name;

    /**
     * Maximum number of people who can participate at the Attraction.
     */
    private Integer capacity;

    /**
     * Instructor, who holds the Attraction. Each Attraction has only one Instructor.
     */
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    /**
     * List of Guests who are signed up at the Attraction. At one Attraction many Guests can participate.
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "attraction_guests",
            joinColumns = @JoinColumn(name ="attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id"))
    public List <Guest> guestList;

    /**
     * Price of one ticket (without discounts).
     */
    public double price;

    /**
     * Exact location within the zoo, where the Attraction is held.
     */
    public String location;

    /**
     * Day when the Attraction is held.
     */
    public Weekday day;

    /**
     * Constructor - constructs and initializes an Attraction. <br>
     * Includes the generation of the Attraction ID.
     * @param name String - name of the Attraction
     * @param capacity Integer - maximum number of guests who can participate
     * @param instructor Instructor - who holds the Attraction
     * @param guestList list of Guests - who signed up at the Attraction
     * @param price Double - price of one ticket
     * @param location String - name of the area where the Attraction is held
     * @param day Weekday - when the Attraction takes place
     * <br><br>The generated ID contains the following elements:
     * <ol>
     *     <li>first letter from the Attraction name</li>
     *     <li>first letter from the Attraction location</li>
     *     <li>first 3 letters from the day of the Attraction</li>
     * </ol>
     */
    public Attraction(String name, Integer capacity, Instructor instructor, List<Guest> guestList, double price, String location, Weekday day) {
        this.name = name;
        this.capacity = capacity;
        this.instructor = instructor;
        this.guestList = guestList;
        this.price = price;
        this.location = location;
        this.day = day;
        // create ID
        // this.ID = UUID.randomUUID().toString();
        this.ID = this.name.substring(0,1)+this.location.substring(0,1)+ '-' +this.day.toString().substring(0,3);
    }

    /**
     * Constructor - constructs and initializes an Attraction without having previously signed up guests. <br>
     * Includes the generation of the Attraction ID. <br>
     * List of Guests is initially an empty list.
     * @param name String - name of the Attraction
     * @param capacity Integer - maximum number of Guests who can participate
     * @param instructor Instructor - who holds the Attraction
     * @param price Double - price of one ticket(without discount)
     * @param location String - name of the area where the Attraction is hold
     * @param day Weekday - when the Attraction takes place
     * <br><br>The generated ID contains the following elements:
     * <ol>
     *     <li>first letter from the Attraction name</li>
     *     <li>first letter from the Attraction location</li>
     *     <li>first 3 letters from the day of the Attraction</li>
     * </ol>
     */
    public Attraction(String name, Integer capacity, Instructor instructor, double price, String location, Weekday day) {
        this.name = name;
        this.capacity = capacity;
        this.instructor = instructor;
        // initially no guests
        this.guestList = new ArrayList<Guest>();
        this.price = price;
        this.location = location;
        this.day = day;
        //this.ID = UUID.randomUUID().toString();
        this.ID = this.name.substring(0,1)+this.location.substring(0,1)+ '-' +this.day.toString().substring(0,3);
    }

    /**
     * Empty constructor.
     */
    public Attraction() {}

    /**
     * This method returns the capacity of the Attraction.
     * @return Integer - the maximum number Guests who can participate at the Attraction
     */
    public Integer getCapacity() {
        return capacity;
    }


    /**
     * This method returns the ID of the Attraction.
     * @return String - generated from the name, location and day of the Attraction
     */
    public String  getID() {
        return ID;
    }

    /**
     * This method returns the Instructor of the Attraction.
     * @return Instructor - who presents the Attraction (show)
     */
    public Instructor getInstructor() {
        return instructor;
    }


    /**
     * This method sets the Instructor of the Attraction equal with the Instructor given as a parameter.
     * @param instructor the new Instructor
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    /**
     * This method returns the number of attending Guests.
     * @return Integer - number of Guests who are signed up for the Attraction.
     */
    public Integer getNrOfGuests(){
        return this.guestList.size();
    }

    /**
     * This method returns the number of available places of the Attraction.
     * @return Integer - the difference between total number of places and occupied places.
     */
    public Integer getNrOfFreePlaces(){
        return this.capacity - this.getNrOfGuests();
    }

    /**
     * This method adds a Guest to the Guest-list of the Attraction.
     * @param guest Guest - who signs up to the Attraction
     */
    public void addGuest(Guest guest) {
        this.guestList.add(guest);
    }

    /**
     * The ordering in this class will be made in lexicographical order.
     * @param o the object to be compared.
     * @return <ul>
     *  <li>a positive value -> when the Attraction given as a parameter is lexicographically less than the other Attraction</li>
     *  <li>a negative value -> when the Attraction given as a parameter is lexicographically greater than the other Attraction</li>
     *  <li>0 -> when the two Attractions have the same name</li>
     * </ul>
     */
    @Override
    public int compareTo(Attraction o) {
        return this.name.compareTo(o.name);
    }

    /**
     * This method converts the Attraction object to a String.
     * @return A String containing information about the Attraction: <br>
     * <ul>
     *     <li>ID</li>
     *     <li>name of Attraction</li>
     *     <li>name of Instructor who holds the show</li>
     *     <li>capacity</li>
     *     <li>price</li>
     *     <li>location</li>
     *     <li>day</li>
     * </ul>
     * Additionally, information is included regarding the signed up guests:
     * <ul>
     *     <li>remaining free places</li>
     *     <li>number of signed up guest</li>
     * </ul>
     */
    @Override
    public String toString() {
        return  "ID=" + ID +
                "  \tname=" + name +
                "  \tinstructor=" + instructor.getData() +
                "  \tprice=" + price +
                "  \tlocation=" + location +
                "  \tday=" + day +
                "  \tcapacity=" + capacity +
                "  \tfree places=" + this.getNrOfFreePlaces() +
                "  \tsigned up guests=" + this.getNrOfGuests() +
                '\n';
    }
}
