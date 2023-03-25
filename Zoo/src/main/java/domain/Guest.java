package domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * This class extends the class Person and models Guests of the zoo.
 */
@Entity
public class Guest extends Person implements Comparable<Guest>{
    /**
     * Guest's birthday (YEAR-MONTH-DAY).
     */
    private LocalDate birthday;

    /**
     * List of Attractions where the Guest is signed up.<br>
     * One Guest can attend more than one Attraction.
     */
    @ManyToMany(mappedBy = "guestList")
    List<Attraction>  attractions;

    /**
     * Constructor - constructs and initializes a Guest. <br>
     * Also uses the constructor from the class Person (super-class) for the attributes: username, firstName, lastName, password.
     * @param username Guest's username
     * @param firstName Guest's first name
     * @param lastName Guest's last name
     * @param password Guest's password
     * @param birthday Guest's birthday
     * @param finalSum sum which must be paid by the Guest for the Attractions
     * @param attractions list of Attractions where the Guest is signed up
     */
    public Guest(String username, String firstName, String lastName, String password, LocalDate birthday, double finalSum, List<Attraction> attractions) {
        super(username, firstName, lastName, password, finalSum);
        this.birthday = birthday;
        this.attractions = attractions;
    }

    /**
     * Constructor - constructs and initializes a Guest without being previously signed up to Attractions. <br>
     * List of Attractions is initially an empty list. <br>
     * Also uses the constructor from the class Person(super-class) for the attributes: username, firstName, lastName, password.
     * @param username Guest's username
     * @param firstName Guest's first name
     * @param lastName Guest's last name
     * @param password Guest's password
     * @param birthday Guest's birthday
     */
    public Guest(String username, String firstName, String lastName, String password, LocalDate birthday) {
        super(username, firstName, lastName, password);
        this.birthday = birthday;
        this.attractions = new ArrayList<Attraction>();
    }

    /**
     * Empty constructor
     */
    public Guest(){}

    /**
     * This method returns a String containing selected information about the Guest.
     * @return A String containing information about the Guest:
     * <ul>
     *    <li>ID</li>
     *    <li>name</li>
     *    <li>age</li>
     * </ul>
     * <br> Also includes succinct information about the Attractions on which the Guest is signed up:
     * <ul>
     *     <li>name of the Attraction</li>
     *     <li>day of Attraction</li>
     *     <li>Instructor of Attraction</li>
     * </ul>
     */
    @Override
    public String getData() {
        String attractions_name = "";
        for (Attraction a: attractions)
            attractions_name = attractions_name + '(' + a.name + " " + a.day + ", " + a.getInstructor().getName() + "), ";

        String data = "ID: " + ID + ", Name: " + firstName + ' ' + lastName + ", Alter: " + this.getAge() +'\n';
        if (!attractions_name.equals("")){
            attractions_name = attractions_name.substring(0, attractions_name.length() - 2);
            data = data + "nimmt teil an: " + attractions_name + ".\n";
        }
        return data;
    }

    /**
     * This method calculates and returns the age of the Guest.
     * @return The actual age of the Guest
     */
    public Integer getAge() {
        return Period.between(this.birthday, LocalDate.now()).getYears();
    }

    /**
     * This method returns the list of Attractions where the Guest is signed up.
     * @return The list of Attractions where the Guest is signed up
     */
    public List<Attraction> getAttractions() {
        return attractions;
    }

    /**
     * This method sets the list of the Attractions equal to the list given as a parameter.
     * @param attractions List of Attractions
     */
    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    /**
     * This method sets the birthday of the Guest equal to the date given as a parameter.
     * @param birthday Localdate - the birthday of the Guest
     */
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * This method adds an Attraction to the Guest's list of Attractions.
     * @param attraction Attraction on which the Guest is signed up
     */
    public void addAttraction(Attraction attraction){
        this.attractions.add(attraction);
        calculateSum();
    }

    /**
     * This method removes the Attraction given as a parameter from the Guest's list of Attractions.
     * @param attraction Attraction on which the Guest can't participate
     */
    public void removeAttraction(Attraction attraction){
        this.attractions.remove(attraction);
        calculateSum();
    }

    /**
     * This method returns the birthday of the Guest.
     * @return Localdate - the birthday of the Guest
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * This method calculates and updates the sum which has to be paid by the Guest for the Attractions
     * on which he/she signed up. <br>
     * The method takes into consideration the age of the Guest and offers discounts for different age groups:
     * <ol>
     *     <li>age above 60 -> 80% of total price <br> </li>
     *     <li>age below 18 -> 50% of total price <br> </li>
     *     <li>age between 18 and 60 -> total price (no discount) </li>
     * </ol>
     */
    @Override
    public void calculateSum() {
        int age = this.getAge();
        double sum = 0.0;
        for (Attraction a: attractions){
            sum += a.price;
        }
        if (age > 60){
            setFinalSum(sum * 0.8);
        }
        else if (age < 18)
        {
            setFinalSum(sum * 0.5);
        }
        else
            setFinalSum(sum);
    }

    /**
     * This class will be sorted in descending order by the Guest’s paid sum for the Attractions.
     * @param o the object to be compared.
     * @return <ul>
     *  <li>A positive value -> when the Guest given as a parameter has smaller sum to pay</li>
     *  <li>A negative value -> when the Guest given as a parameter has a higher sum to pay</li>
     *  <li>0 -> when the two Guests have the same sum to pay for the Attractions</li>
     * </ul>
     */
    @Override
    public int compareTo(Guest o) {
        if (this.finalSum > o.finalSum) return 1;
        else if (this.finalSum == o.finalSum) return 0;
        else return -1;
    }

    /**
     * This method converts the Guest object to a String, using the toString method from the class Person(superclass).
     * @return A String containing the ID, name, birthday and sum to be paid for the signed up Attractions
     */
    @Override
    public String toString() {
        return super.toString() + '\t' +
                "birthday=" + birthday + '\n';
    }
}
