package registration;

import domain.Attraction;
import domain.Guest;
import domain.Instructor;
import domain.Weekday;
import repository.AttractionRepository;
import repository.GuestRepository;
import repository.InstructorRepository;
import utils.NoMoreAvailableTicketsException;
import utils.NoSuchDataException;
import utils.BadInputException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller - Creates the connection between repositories and view
 */
public class RegistrationSystem {
    private final AttractionRepository attractionRepository;
    private final GuestRepository guestRepository;
    private final InstructorRepository instructorRepository;

    /**
     * Constructor - constructs and initializes a RegistrationSystem. <br>
     * @param instructorRepository InstructorRepository
     * @param attractionRepository AttractionRepository - populated with Attractions who have Instructors from the InstructorRepository
     * @param guestRepository GuestRepository - populated with Guests who are signed to Attractions from the AttractionRepository
     */
    public RegistrationSystem(AttractionRepository attractionRepository, GuestRepository guestRepository, InstructorRepository instructorRepository) {
        this.attractionRepository = attractionRepository;
        this.guestRepository = guestRepository;
        this.instructorRepository = instructorRepository;
    }

    /**
     * This method returns the list of attractions from the AttractionRepository.
     * @return The list of attractions from the AttractionRepository
     */
    public List<Attraction> getAllAttractions() {
        return this.attractionRepository.getAllAttractions();
    }

    /**
     * This method adds an attraction to the zoo. <br>
     * The Instructor of the Attraction is set and at this Instructors' list of Attractions the added Attraction also has to appear.
     * @param attraction Attraction - which is going to be added to the zoo.
     * @param idInstructor String - the ID of the Instructor who will hold the Attraction
     * @return Boolean - true if the attraction could be added to the zoo, false otherwise <br>
     * Possible causes when the method returns false:
     * <ul>
     *     <li>Instructor doesn't exist with the given ID</li>
     *     <li>The attraction is already added to the AttractionRepository</li>
     * </ul>
     */
    public boolean addAttraction(Attraction attraction, String idInstructor) {
        Instructor instructor = this.findInstructorByUsername(idInstructor);
        // instructor with the given ID must exist
        // attraction doesn't appear previously in the list of attractions
        if (instructor != null && attraction!= null && this.attractionRepository.findByID(attraction.getID()) == null) {
            attraction.setInstructor(instructor);
            this.attractionRepository.add(attraction);
            // attraction must appear at the attractionlist of the instructor too
            instructor.addAttraction(attraction);
            this.instructorRepository.update(instructor.getID(), instructor);
            return true;
        }
        return false;
    }

    /**
     * This method filters the Attractions with available places. <br>
     * The filtering condition is that the capacity of the show is greater than the number of signed up guests. <br>
     * NoSuchDataException exception is thrown (and then caught) when there are no Attractions with available places.
     * @return The list of Attractions with available places.
     */
    public List<Attraction> getAllAttractionsWithFreePlaces() {
        List<Attraction> attractionsWithFreePlaces = this.attractionRepository.getAllAttractions()
                .stream()
                .filter(attr -> attr.getCapacity() > attr.getNrOfGuests())
                .toList();

        if (attractionsWithFreePlaces.size() == 0)
            try {
                throw new NoSuchDataException("Keine Attraktionen gefunden");
            } catch (NoSuchDataException e) {
                System.out.println(e.getMessage());
            }
        return attractionsWithFreePlaces;

//        without stream:
//        List<Attraction> attractionsWithFreePlaces = new ArrayList<>();
//        for (Attraction attraction : this.attractionRepository.getAllAttractions())
//            if (attraction.getCapacity() > attraction.getNrOfGuests())
//                attractionsWithFreePlaces.add(attraction);

    }

    /**
     * This method sorts the Attractions lexicographically by title.
     * @return The list of Attractions sorted lexicographically by title
     */
    public List<Attraction> getAttractionsSortedByTitle() {
        List<Attraction> sortedAttractions = this.attractionRepository.getAllAttractions();
        Collections.sort(sortedAttractions);
        return sortedAttractions;
    }

    /**
     * This method filters the Attractions held later or on the given weekday as a parameter. <br>
     * The filtering condition is that the number of the attraction-day is greater than the number of the given weekday.
     * @param weekday Weekday - we search the attractions held later or on this day.
     * @return The list of Attractions held later or on the given weekday as a parameter.
     */
    public List<Attraction> getAttractionsAfterAGivenDay(Weekday weekday) {
        List<Attraction> attractionsAfterADay = new ArrayList<>();
        try {
            attractionsAfterADay = this.attractionRepository.getAllAttractions()
                    .stream()
                    .filter(attr -> attr.day.getNr() >= weekday.getNr())
                    .toList();
            } catch (NullPointerException ignored) {}
        return attractionsAfterADay;

//        without stream:
//            for (Attraction attr : this.attractionRepository.getAllAttractions()) {
//                if (attr.day.getNr() >= weekday.getNr())
//                    attractionsAfterADay.add(attr);
    }

    /**
     * This method sorts the Attractions ascending by price. The method uses a new Comparable for this.
     * @return The list of Attractions sorted ascending by price
     */
    public List<Attraction> getAttractionsSortedByPriceAscending() {
        List<Attraction> sortedAttractions = this.attractionRepository.getAllAttractions();
        Collections.sort(sortedAttractions,
                (Attraction a1, Attraction a2) -> Double.compare(a1.price, a2.price));
        /*
        Collections.sort(sortedAttractions, new Comparator<Attraction>() {
            @Override
            public int compare(Attraction a1, Attraction a2) {
                return Double.compare(a1.price, a2.price);
            }
        }); */
        return sortedAttractions;
    }

    /**
     * This method sorts the Attractions ascending by signed up Guests-number.
     * @return The list of Attractions sorted ascending by Guest-number
     */
    public List<Attraction> getAttractionsSortedByGuestAscending() {
        List<Attraction> sortedAttractions = this.attractionRepository.getAllAttractions();
        Collections.sort(sortedAttractions,
                (Attraction o1, Attraction o2) -> {
                    return o1.getNrOfGuests() - o2.getNrOfGuests();
                }
        );
        /*
        Collections.sort(sortedAttractions, new Comparator<Attraction>() {
            @Override
            public int compare(Attraction o1, Attraction o2) { return o1.getNrOfGuests() - o2.getNrOfGuests();     }
        }); */
        return sortedAttractions;
    }

    /**
     * This method filters the Attractions which price is cheaper or equal to a given price as a parameter. <br>
     * The filtering condition is that the normal price of the attraction is less than or equal to the given price. <br>
     * NoSuchDataException exception is thrown (and then caught) when there are no Attractions with the given criteria
     * @param givenPrice Double - a price below we search Attractions
     * @return The list of Attractions which are less than or equal to a given price.
     */
    public List<Attraction> filterAttractionsByAGivenValue(double givenPrice) {
        List<Attraction> attractionsWithFixedPrice =
                this.attractionRepository.getAllAttractions()
                        .stream()
                        .filter(attr -> attr.price <= givenPrice)
                        .toList();

        if (attractionsWithFixedPrice.size() == 0)
            try {
                throw new NoSuchDataException("Keine Attraktionen gefunden");
            } catch (NoSuchDataException e) {
                System.out.println(e.getMessage());
            }
        return attractionsWithFixedPrice;
//      without stream:
//      List<Attraction> attractionsWithFixedPrice2 = new ArrayList<>();
//      for (Attraction attraction : this.attractionRepository.getAllAttractions())
//            if (attraction.price <= givenPrice)
//                attractionsWithFixedPrice2.add(attraction);

    }

    /**
     * This method returns the list of Guests signed up for an Attraction.
     * @param idAttraction String - the id of the Attraction which signed up guests we search
     * @return The list of Guests who are signed up to the Attraction, or null if there is no Attraction with the given ID
     */
    public List<Guest> getGuestsOfAttraction(String idAttraction) {
        Attraction attraction = this.attractionRepository.findByID(idAttraction);
        if (attraction != null)
            return attraction.guestList;
        else
            return null;
    }

    /**
     * This method calculates and returns the average income of Instructors. <br>
     * First the sum of total incomes is calculated and then divided by the number of Instructors.
     * @return Double - the average income of Instructors
     */
    public double getAverageSalaryOfInstructors() {
        double avgSum = 0;
        for (Instructor instructor: this.getAllInstructors()) {
            avgSum += instructor.getFinalSum();
        }
        avgSum = avgSum / this.getAllInstructors().size();
        return avgSum;
    }

    /**
     * This method filters the Instructors who have higher salary than the average Instructor. <br>
     * This method uses the method getAverageSalaryOfInstructors() to compare this value to each instructors' salary.
     * The filtering condition is that the Instructor's salary is higher than the average income.
     * @return The list of Instructors who have higher income than the average Instructor
     */
    public List<Instructor> filterInstructorsWithHigherSalaryThanAverage() {
        double avgSum = this.getAverageSalaryOfInstructors();
        List<Instructor> instructorsWithHighSalary = this.instructorRepository.getAllInstructors()
                .stream()
                .filter(i -> i.getFinalSum() > avgSum)
                .toList();
        return instructorsWithHighSalary;

        // List<Instructor> instructorsWithHighSalary = new ArrayList<>();
//        double avgSum = this.getAverageSalaryOfInstructors();
//        for (Instructor instructor : this.instructorRepository.getAllInstructors())
//            if (instructor.getFinalSum() > avgSum)
//                instructorsWithHighSalary.add(instructor);
    }

    /**
     * This method adds a Guest to the GuestRepository. <br>
     * @param guest Guest - who is going to be added.
     * @return Boolean - true if the guest could be added, false otherwise <br>
     */
    public boolean addGuest(Guest guest) {
        int guestsInitialNr = this.guestRepository.getAllGuests().size();
        this.guestRepository.add(guest);
        return this.guestRepository.getAllGuests().size() == guestsInitialNr + 1;
    }

    /**
     * This method returns the list of Guests from the GuestRepository.
     * @return The list of Guests from the GuestRepository
     */
    public List<Guest> getAllGuests() {
        return this.guestRepository.getAllGuests();
    }

    /**
     * This method returns the list of Attractions of a Guest whose username is given as a parameter.<br>
     * NoSuchDataException exception is thrown (and then caught) when there are no Attractions with the given criteria
     * @param idGuest String - the username of the Guest whose Attractions we search
     * @return The list of Attractions where the Guest is signed up
     */
    public List<Attraction> getAttractionsOfGuest(String idGuest) {
        List<Attraction> attractions = null;
        Guest guest = this.guestRepository.findByID(idGuest);
        try {
            attractions = guest.getAttractions();
            if (attractions.size() == 0)
                throw new NoSuchDataException("Keine Attraktionen gefunden");
        } catch (NoSuchDataException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Besucher mit dem angegebenen Benutzernamen existiert nicht");
        }
        return attractions;
    }

    /**
     * This method returns the list of attractions held by the Instructor whose ID is given as a parameter. <br>
     * NoSuchDataException exception is thrown (and then caught) when there are no Attractions with the given criteria.
     * @param idInstructor String - the ID of the Instructor whose Attractions we are searching
     * @return The list of Attractions held by the given Instructor
     */
    public List<Attraction> getAttractionsOfInstructor(String idInstructor) {
        List<Attraction> attractions = null;
        Instructor instructor = this.instructorRepository.findByID(idInstructor);
        try {
            attractions = instructor.getAttractions();
            if (attractions.size() == 0)
                throw new NoSuchDataException("Keine Attraktionen gefunden");
        } catch (NoSuchDataException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Instruktor mit dem angegebenen Benutzernamen existiert nicht");
        }
        return attractions;
    }


    /**
     * This method return the sum which has to be paid by the Guest whose username is given as a parameter.
     * @param idGuest String - the username of the searched Guest
     * @return Double - the sum which has to be paid by the given Guest
     */
    public double getFinalSumOfGuest(String idGuest) {
        Guest guest = this.guestRepository.findByID(idGuest);
        if (guest != null)
            return guest.getFinalSum();
        return 0;
    }

    /**
     * This method sorts the Guests in descending order by the sum which they have to pay.
     * @return The list of Guests sorted in descending order by the sum which they have to pay.
     */
    public List<Guest> getGuestsSortedDescendingBySum() {
        List<Guest> guests = this.guestRepository.getAllGuests();
        Collections.sort(guests, new Comparator<Guest>() {
            @Override
            public int compare(Guest o1, Guest o2) {
                if (o1.getFinalSum() < o2.getFinalSum()) return 1;
                else if (o1.getFinalSum() == o2.getFinalSum()) return 0;
                else return -1;
            }
        });
        // guests.sort(Collections.reverseOrder());
        return guests;
    }

    /**
     * This method returns the Guest object who has the given username as a parameter.
     * @param username String - the username of the searched Guest
     * @return Guest - who has the given username or null, if there is no Guest with the given username
     */
    public Guest findGuestByUsername(String username) {
        return this.guestRepository.findByID(username);
    }

    /**
     * This method returns the Instructor object who has the given id as a parameter.
     * @param username String - the username of the searched Guest
     * @return Instructor - who has the given username or null, if there is no Instructor with the given ID
     */
    public Instructor findInstructorByUsername(String username) {
        return this.instructorRepository.findByID(username);
    }

    /**
     * This method adds an Instructor to the InstructorRepository. <br>
     * @param instructor Instructor - who is going to be added.
     * @return Boolean - true if the guest could be added, false otherwise <br>
     */
    public boolean addInstructor(Instructor instructor) {
        int instructorsInitialNr = this.instructorRepository.getAllInstructors().size();
        this.instructorRepository.add(instructor);
        return this.instructorRepository.getAllInstructors().size() == instructorsInitialNr + 1;
    }

    /**
     * This method returns the list of Instructors from the InstructorRepository.
     * @return The list of Instructors from the InstructorRepository
     */
    public List<Instructor> getAllInstructors() {
        return this.instructorRepository.getAllInstructors();
    }

    /**
     * This method returns the income of an Instructor.
     * @param idInstructor String - ID of the Instructor whose income we search
     * @return Double - the sum which gets the instructor from the guests who are signed up to the instructor's attractions
     */
    public double getSumFromGuests(String idInstructor) {
        Instructor instructor = this.findInstructorByUsername(idInstructor);
        return instructor.getFinalSum();
    }

    /**
     * This method returns the income of the Zoo. The value represents the sum of the Instructors' income.
     * @return Double - the income of the Zoo
     */
    public double getIncomeOfTheZoo() {
        double sum=0;
        for (Instructor instructor: this.getAllInstructors()){
            sum += instructor.getFinalSum();
        }
        return sum;
    }

    /**
     * This method changes the Instructor of an Attraction. <br>
     * In the Attraction list of the old Instructor will disappear the Attraction, and appear at the new Instructor. <br>
     * The changes must appear in the signed up Guests' Attraction list as well. <br>
     * The income of the Instructors will be updated properly.
     * @param idAttraction - The ID of the Attraction whose Instructor is going to be changed
     * @param idNewInstructor - The ID of the Instructor who will hold the Attraction
     * @return Boolean - true if the change is successful, false otherwise <br>
     * Possible causes when the method returns false:
     * <ol>
     *     <li>Attraction with the given ID doesn't exist</li>
     *     <li>Instructor with the given ID doesn't exist</li>
     * </ol>
     */
    public boolean changeInstructorOfAttraction(String idAttraction, String idNewInstructor) {
        Attraction attr = this.attractionRepository.findByID(idAttraction);
        Instructor newInstructor = this.instructorRepository.findByID(idNewInstructor);
        if (attr != null && newInstructor != null) {
            Instructor oldInstructor = attr.getInstructor();
            oldInstructor.removeAttraction(attr);
            attr.setInstructor(newInstructor);
            newInstructor.addAttraction(attr);
            return true;
        }
        return false;
    }

    /**
     * This method realizes the sign-up of a Guest to an Attraction if there are available places. <br>
     * In the Attraction list of the Guest will appear the Attraction, as well as the Guest in the Guest list of the Attraction. <br>
     * The income of the Instructor increases, as well as the sum which the Guest has to pay. <br>
     * NoMoreAvailableTicketsException exception is thrown (and then caught) when there are no more available places at the selected Attraction.
     * @param idGuest String - the ID of the Guest who wants to sign up to an Attraction
     * @param idAttraction String - the ID of the Attraction on which the Guest would like to sign up
     * @return Boolean - true if the sign-up is successful, false otherwise <br>
     * Possible causes when the method returns false:
     * <ol>
     *     <li>Attraction with the given ID doesn't exist</li>
     *     <li>There are no more available tickets</li>
     *     <li>Guest with the given username doesn't exist</li>
     *     <li>Guest is already signed up to the attraction</li>
     * </ol>
     */
    public boolean signUpForAttraction(String idGuest, String idAttraction) {
        Attraction attr = this.attractionRepository.findByID(idAttraction);
        if (attr != null) {
            if (attr.getNrOfFreePlaces() > 0) {
                Guest g = this.guestRepository.findByID(idGuest);
                // if guest is already signed up -> sign up not possible
                if (g != null && !attr.guestList.contains(g)) {
                    g.addAttraction(attr);
                    attr.addGuest(g);
                    attr.getInstructor().calculateSum();
                    return true;
                }
            } else try {
                throw new NoMoreAvailableTicketsException("Wir haben nicht mehr Platz");
            } catch (NoMoreAvailableTicketsException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    /**
     * This method deletes an Attraction of an Instructor. <br>
     * The process will be only be successful if the given Instructor ID belongs to the Instructor who holds the show.<br>
     * The Attraction won't appear anymore in the list of Attractions for any of the guests. <br>
     * The income of the Instructor and the sum which the Guests have to pay are updated properly.
     * @param idInstructor String - the ID of the Instructor who wants to delete an Attraction
     * @param idAttraction String - the ID of the Attraction which will be deleted
     * @return Boolean - true if the Attraction could be deleted, false otherwise <br>
     * Possible causes when the method returns false:
     * <ol>
     *     <li>Attraction with the given ID doesn't exist</li>
     *     <li>The given Instructor ID doesn't belong to the actual Instructor of the Attraction</li>
     *     <li>Guest with the given username doesn't exist</li>
     * </ol>
     */
    public boolean deleteAttraction(String idInstructor, String idAttraction) {
        Attraction attr = this.attractionRepository.findByID(idAttraction);
        if (attr != null && attr.getInstructor().getID().equals(idInstructor)) {
            Instructor instructor = this.instructorRepository.findByID(idInstructor);
            instructor.removeAttraction(attr);
            this.attractionRepository.delete(idAttraction);

            for (Guest guest: this.guestRepository.getAllGuests()) {
                guest.removeAttraction(attr);
                this.guestRepository.update(guest.getID(), guest);
            }
            return true;
        }
        return false;
    }

    /**
     * This method returns a Guest object if all the Guest-attributes are correct.
     * @param username Username of the Guest
     * @param firstName First name of the Guest
     * @param lastName Last name of the Guest
     * @param birthday Birthday of the Guest
     * @param password Password of the Guest
     * @return Guest - with the given attributes if they are correct, null otherwise.
     * Possible causes when the method returns null:
     * <ul>
     *     <li>Instructor doesn't exist with the given ID</li>
     *     <li>The attraction is already added to the Attractionrepository</li>
     * </ul>
     */
    public Guest verifiedUserInputGuest(String username, String firstName, String lastName, String birthday, String password) {
        boolean correctInput = this.verifyUserInputNameAndPassword(username, firstName, lastName, password);
        if (correctInput){
            try {
                LocalDate birthdayDate = LocalDate.parse(birthday);
                return new Guest(username, firstName, lastName, password, birthdayDate);
            } catch (DateTimeParseException e){
                System.out.println("Gib das Geburtsdatum im Format Jahr-Monat-Tag an");
            }
        }
        return null;
    }

    /**
     * This method verifies if the username, firstname and lastname inputs are correct. <br>
     * BadInputException exception is thrown (and then caught) when the input is incorrect.
     * @param username String - which was given as username
     * @param firstName String - which was given as firstName
     * @param lastName String - which was given as lastName
     * @param password String - which was given as password
     * @return Boolean - true if the input is correct, false otherwise <br>
     * Possible causes when BadInputException appears and the method returns false:
     * <ol>
     *     <li>Username contains big letters too</li>
     *     <li>The name contains other characters than letters</li>
     *     <li>Password length is shorter than 3 characters</li>
     * </ol>
     */
    public boolean verifyUserInputNameAndPassword(String username, String firstName, String lastName, String password) {
        boolean correctInput = true;
        try {
            if (!username.toLowerCase().equals(username))
                throw new BadInputException("Der Benutzername kann nur kleine Buchstaben enthalten");
            if ((firstName + lastName).matches(".*[@#$%^&*0-9].*"))
                throw new BadInputException("Der Nachname und Vorname kann nur Buchstaben enthalten");
            if (password.length() < 3)
                throw new BadInputException("Das Passwort muss mindestens 3 Character enthalten");

        } catch (BadInputException e) {
            System.out.println(e.getMessage());
            correctInput = false;
        }
        return correctInput;
    }

    /**
     * This method verifies if the weekday input is correct. This means it matches any element of the Weekday Enum.
     * @param day String - which was given as weekday
     * @return Boolean - true if the input is correct, false otherwise <br>
     */
    public Weekday verifiedUserInputWeekday(String day){
        Weekday weekday = null;
        try {
            weekday = Weekday.valueOf(day.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Der Tag kann sein: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday oder Sunday");
        }
        return weekday;
    }

    /**
     * This method verifies if the price input is correct. This means it can be converted to a Double value.
     * @param price String - which was given as price
     * @return Boolean - true if the input is correct, false otherwise
     */
    public double verifiedUserInputPrice(String price){
        double priceAttr = 0.0;
        try {
            priceAttr = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            System.out.println("Das Preis muss eine rationale Zahl sein");
        }
        return priceAttr;
    }

    /**
     * This method verifies if the input for creating an Attraction is correct. <br>
     * Therefore name, capacity, price, location and weekday inputs must be correct.
     * @param name String - which was given as name
     * @param capacity String - which was given as capacity
     * @param price String - which was given as price
     * @param location String - which was given as location
     * @param weekday String - which was given as weekday
     * @return Attraction - with the given attributes if they are correct, null otherwise.
     */
    public Attraction verifiedUserInputAttraction(String name, String capacity, String price, String location, String weekday) {
        boolean correctInput = true;
        int capacityAttr = 0;
        try {
            capacityAttr = Integer.parseInt(capacity);
        } catch (NumberFormatException e) {
            System.out.println("Die Kapazität muss eine ganze Zahl sein");
            correctInput = false;
        }

        Weekday weekdayAttr = this.verifiedUserInputWeekday(weekday);
        double priceAttr = this.verifiedUserInputPrice(price);
        if (weekdayAttr == null || priceAttr == 0.0)
            correctInput = false;

        if (correctInput)
            return new Attraction(name, capacityAttr, null, priceAttr, location, weekdayAttr);
        return null;
    }
}