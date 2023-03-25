package domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This abstract class models a Person.
 */
@MappedSuperclass
abstract class Person {
    /**
     * Unique identification of the Person.
     */
    @Id
    protected String ID;

    /**
     * A sum of money associated with the Person.
     */
    protected double finalSum;

    /**
     * Credential of the Person.
     */
    protected String password;

    /**
     * First name of the Person.
     */
    protected String firstName;

    /**
     * Last name of the Person.
     */
    protected String lastName;

    /**
     * Constructor - constructs and initializes a Person with a given sum. <br>
     * @param id Person's id
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param password Person's password
     * @param sum Person's sum of money
     */
    public Person(String id, String firstName, String lastName, String password, double sum) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.finalSum = sum;
        this.ID = id;
        this.password = password;
    }

    /**
     * Constructor - constructs and initializes a Person without a given sum. <br>
     * Sum is initially 0. <br>
     * @param id Person's id
     * @param firstName Person's first name
     * @param lastName Person's last name
     * @param password Person's password
     */
    public Person(String id, String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.finalSum = 0;
        this.ID = id;
        this.password = password;
    }

    /**
     * Empty constructor.
     */
    public Person(){}

    /**
     * This method returns a String containing succinct information about the instructor.
     * @return A String with succinct information about the object.
     */
    abstract String getData();

    /**
     * This method calculates and updates the sum of the Person.
     */
    public abstract void calculateSum();

    /**
     * This method returns the ID of the Person.
     * @return the ID of the Person
     */
    public String getID() {
        return ID;
    }

    /**
     * This method returns the first name of the Person.
     * @return the first name of the Person
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method sets the first name of the Person equal to the String given as a parameter.
     * @param firstName String - The first name of the Person
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * This method returns the last name of the Person.
     * @return the lastname of the Person
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method sets the last name of the Person equal to the String given as a parameter.
     * @param lastName String - The last name of the Person
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * This method returns the name of the Guest (concatenating first name with last name).
     * @return String - Name of the Guest
     */
    public String getName(){
        return this.firstName + ' ' + this.lastName;
    }


    /**
     * This method returns the sum of the Person.
     * @return Double - the sum which the Guest has to pay
     */
    public double getFinalSum() {
        return finalSum;
    }

    /**
     * This method sets the finalSum of the Person equal to the value given as a parameter.
     * @param sum Double - The sum of the Person
     */
    public void setFinalSum(double sum){
        finalSum = sum;
    }

    /**
     * This method converts the Person object to a String.
     * @return A String containing the id, name, and sum.
     */
    @Override
    public String toString() {
        return "ID=" + ID  +
                "  firstName=" + firstName +
                "  lastName=" + lastName +
                "  finalSum=" + finalSum;
    }

    /**
     * This method returns the password of the Person.
     * @return String - password of the Person
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method sets the password of the Person equal to the String given as a parameter.
     * @param password String - the password of the Person
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Verifies if a given String is equal to the person's password.
     * @param password String - password which has to be verified
     * @return Boolean - True if the given password is correct, otherwise false
     */
    public boolean matchesPassword(String password){
        return this.password.equals(password);
    }
}
