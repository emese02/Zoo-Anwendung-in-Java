package repository.memoryRepo;

import domain.Attraction;
import domain.Guest;
import repository.AttractionRepository;
import repository.GuestRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * InMemoryGuestRepository implements the interface InstructorRepository. The data is saved in memory.
 */
public class InMemoryGuestRepository implements GuestRepository {
    /**
     * List of Guests
     */
    private List<Guest> allGuests;
    /**
     * AttractionRepository from where the Attractions are selected on which the Guests can sign up.
     */
    private final AttractionRepository attractionRepository;

    /**
     * Constructor - constructs and initializes an InMemoryGuestRepository. <br>
     * Initially the list of Guests is empty, then the method populateGuests() is called.
     * @param attractionRepository AttractionRepository from where the Attractions are selected on which the Guests can sign up
     */
    public InMemoryGuestRepository(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
        this.allGuests = new ArrayList<Guest>();
        this.populateGuests();
    }

    /**
     * This method populates with data the list of Guests with the help of add() method. <br>
     * When an Attraction is added to the Guest's list of Attraction, the Guest appears in the Guest-list of the Attraction as well. <br>
     * The Instructor's income will also increase.
     */
    private void populateGuests(){
        List<Attraction> attractions = attractionRepository.getAllAttractions();

        Attraction attraction1 = attractions.get(0);
        Attraction attraction2 = attractions.get(1);
        Attraction attraction3 = attractions.get(2);
        Attraction attraction4 = attractions.get(3);
        Attraction attraction5 = attractions.get(4);
        Attraction attraction6 = attractions.get(5);
        Attraction attraction7 = attractions.get(6);
        Attraction attraction8 = attractions.get(7);

        Guest guest1 = new Guest("maria01", "Maria", "Kis", "KM01", LocalDate.of(2002,2,1));
        Guest guest2 = new Guest("ioana.petru","Ioana", "Petru",  "1b38TC1li", LocalDate.of(1997,2,1));
        Guest guest3 = new Guest("comsa_ana","Ana", "Comsa", "abcd123", LocalDate.of(1967,3,1));
        Guest guest4 = new Guest("pop.oti","Otilia", "Pop", "animals", LocalDate.of(2002,4,11));
        Guest guest5 = new Guest("ion123","Ion", "Ionut", "zoo", LocalDate.of(1989,8,12));
        Guest guest6 = new Guest("timi11","Timea", "Gal", "gtig", LocalDate.of(2009,2,1));
        Guest guest7 = new Guest("g.emese","Emese", "Gal", "bcn10", LocalDate.of(2010,7,16));
        Guest guest8 = new Guest("ecaterinaa","Ecaterina", "Popa", "password123", LocalDate.of(2021,11,18));
        Guest guest9 = new Guest("katy99","Katy", "Perry", "1234", LocalDate.of(2018,3,3));
        Guest guest10 = new Guest("gomez.s","Selena", "Gomez", "selenaa", LocalDate.of(2001,5,28));
        Guest guest11 = new Guest("bieber_justin","Justin", "Bieber", "success", LocalDate.of(2000,3,23));
        Guest guest12 = new Guest("celined","Céline", "Dion", "titanic", LocalDate.of(1970,12,24));
        Guest guest13 = new Guest("leo_dicaprio","Leonardo", "DiCaprio", "titanic", LocalDate.of(1956,8,8));
        Guest guest14 = new Guest("roberts.julia","Julia", "Roberts", "sunshine", LocalDate.of(1999,7,7));
        Guest guest15 = new Guest("tom_hanks","Tom", "Hanks", "summer", LocalDate.of(1999,11,12));
        Guest guest16 = new Guest("gibson_mel","Mel", "Gibson", "abc123", LocalDate.of(1988,2,1));
        Guest guest17 = new Guest("jackie23","Jackie", "Chan", "passw0rd", LocalDate.of(1995,3,16));
        Guest guest18 = new Guest("terence_hill","Terence", "Hill", "111111", LocalDate.of(1988,9,29));

        guest1.addAttraction(attraction8); guest3.addAttraction(attraction8); guest5.addAttraction(attraction8); guest7.addAttraction(attraction8); guest9.addAttraction(attraction8);
        guest11.addAttraction(attraction8); guest13.addAttraction(attraction8); guest15.addAttraction(attraction8); guest17.addAttraction(attraction8); guest18.addAttraction(attraction8);

        attraction8.addGuest(guest1); attraction8.addGuest(guest3); attraction8.addGuest(guest5); attraction8.addGuest(guest7); attraction8.addGuest(guest9);
        attraction8.addGuest(guest11); attraction8.addGuest(guest13); attraction8.addGuest(guest15); attraction8.addGuest(guest17); attraction8.addGuest(guest18);

        guest1.addAttraction(attraction5); guest2.addAttraction(attraction5); guest3.addAttraction(attraction5); guest4.addAttraction(attraction5);

        attraction5.addGuest(guest1); attraction5.addGuest(guest2); attraction5.addGuest(guest3); attraction5.addGuest(guest4);

        this.add(guest1); this.add(guest2); this.add(guest3); this.add(guest4); this.add(guest5);
        this.add(guest6); this.add(guest7); this.add(guest8); this.add(guest9); this.add(guest10);
        this.add(guest11); this.add(guest12); this.add(guest13); this.add(guest14); this.add(guest15);
        this.add(guest16); this.add(guest17); this.add(guest18);

        attraction5.getInstructor().calculateSum();
        attraction8.getInstructor().calculateSum();
    }

    /**
     * This method returns the list of Guests.
     * @return The list of Guests
     */
    @Override
    public List<Guest> getAllGuests() {
        return this.allGuests;
    }

    /**
     * This method adds a Guest to the list of Guests. <br>
     * If there is already a Guest in the repository with the same ID, the new Guest won't be added.
     * @param guest Guest who will be added.
     */
    @Override
    public void add(Guest guest) {
        try {
            for (Guest g: this.allGuests){
                if (g.getID().equals(guest.getID())){
                    System.out.println("Ein Besucher mit dieser ID existiert schon");
                    return;
                }
            }
            this.allGuests.add(guest);
        } catch (NullPointerException ignored) {}
    }

    /**
     * This method deletes a Guest from the list of Instructors.
     * @param id String - the ID of the Guest who will be eliminated
     */
    @Override
    public void delete(String id) {
        Guest guest = this.findByID(id);
        this.allGuests.remove(guest);
    }

    /**
     * This method updates a Guest from the list of Guests. <br>
     * @param id String - the ID of the Guest who will be updated
     * @param guest the new Guest who will appear instead of the old Guest
     */
    @Override
    public void update(String id, Guest guest) {
        Guest g = this.findByID(id);
        int position = this.allGuests.indexOf(g);
        this.allGuests.set(position,guest);
    }

    /**
     * This method returns the Guest who has the ID given as a parameter. <br>
     * @param id String - the ID of the Guest who is searched
     * @return the Guest who has the ID given as a parameter or null if there is no Guest with the given ID
     */
    @Override
    public Guest findByID(String id) {
        for (Guest g: this.allGuests){
            if (g.getID().equals(id))
                return g;
        }
        return null;
    }
}
