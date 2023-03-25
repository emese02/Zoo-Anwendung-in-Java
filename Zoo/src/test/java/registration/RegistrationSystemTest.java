package registration;

import domain.Attraction;
import domain.Guest;
import domain.Instructor;
import domain.Weekday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AttractionRepository;
import repository.GuestRepository;
import repository.InstructorRepository;
import repository.memoryRepo.InMemoryAttractionRepository;
import repository.memoryRepo.InMemoryGuestRepository;
import repository.memoryRepo.InMemoryInstructorRepository;
import utils.NoMoreAvailableTicketsException;
import utils.NoSuchDataException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static domain.Weekday.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Controller.
 */
class RegistrationSystemTest{
    private InstructorRepository instructorRepository;
    private AttractionRepository attractionRepository;
    private GuestRepository guestRepository;
    private RegistrationSystem controller;

    /**
     * For the tests inmemory repositories are used.
     */
    @BeforeEach
    void setUp(){
        this.instructorRepository = new InMemoryInstructorRepository();
        this.attractionRepository = new InMemoryAttractionRepository(instructorRepository);
        this.guestRepository = new InMemoryGuestRepository(attractionRepository);
        this.controller = new RegistrationSystem(attractionRepository, guestRepository, instructorRepository);
    }

    /**
     * Test for getAllAttractions.
     */
    @Test
    void testGetAllAttractions(){
        List<Attraction> attractions = controller.getAllAttractions();
        assertEquals(attractions.size(), 8);
        assertEquals(attractions.get(0).name, "Zoo time");
        assertEquals(attractions.get(7).name, "VIP zoo show");
    }

    /**
     * Test for adding successfully an Attraction.
     * <ul>
     *     <li> the chosen Instructor id belongs to an Instructor in the repository </li>
     *     <li> a new Instructor is added to the repository and after that is chosen as the Instructor of the attraction</>
     * </ul>
     */
    @Test
    void testSuccessfulAddAttraction() {
        // test with existing instructor id
        Attraction attraction1 = new Attraction("Sea lion show", 50, null, 120.4, "Pool1", SUNDAY);
        assertTrue(controller.addAttraction(attraction1, "i2"));

        // add attraction after add new instructor
        Attraction attraction2 = new Attraction("Orca show", 50, null, 100.10, "Pool1", MONDAY);
        Instructor instructor = new Instructor("i10","Samantha", "Thompson", "passwordABC");
        controller.addInstructor(instructor);
        assertTrue(controller.addAttraction(attraction2, "i10"));
    }

    /**
     * Test for adding unsuccessfully an Attraction.
     * <ul>
     *     <li> the chosen Instructor id does not belong to an Instructor in the repository </li>
     *     <li> the attraction is already added to the repository (can't be added twice) </li>
     * </ul>
     */
    @Test
    void testUnSuccessfulAddAttraction() {
        // test with non-existent instructor id
        Attraction attraction1 = new Attraction("Orca show", 50, null, 100.10, "Pool1", MONDAY);
        assertFalse(controller.addAttraction(attraction1, "i15"));

        // add the same attraction twice
        Attraction attraction2 = controller.getAllAttractions().get(1);
        assertFalse(controller.addAttraction(attraction2, "i2"));
        assertFalse(controller.addAttraction(attraction2, "i3"));
    }

    /**
     * Test for getting Attractions with available places.
     * <ul>
     *     <li> size of the list is checked </li>
     *     <li> Attraction with no more available place is not included </li>
     *     <li> Attraction with available places is included </li>
     * </ul>
     */
    @Test
    void testGetAllAttractionsWithFreePlaces() throws NoSuchDataException {
        assertEquals(controller.getAllAttractionsWithFreePlaces().size(), 7);
        // attraction with no more free places is not included
        Attraction attractionWithNoMorePlaces = controller.getAttractionsSortedByTitle().get(3);
        assertEquals(attractionWithNoMorePlaces.getNrOfFreePlaces(),0);
        assertFalse(controller.getAllAttractionsWithFreePlaces().contains(attractionWithNoMorePlaces));

        // attraction with more free places is included
        Attraction attractionWithFreePlaces = controller.getAttractionsSortedByTitle().get(2);
        assertNotEquals(attractionWithFreePlaces.getNrOfFreePlaces(),0);
        assertTrue(controller.getAllAttractionsWithFreePlaces().contains(attractionWithFreePlaces));
    }

    /**
     * Test for getting Attractions sorted by title.
     * <ul>
     *     <li> size of the list is checked </li>
     *     <li> the order is respected </li>
     * </ul>
     */
    @Test
    void testGetAttractionsSortedByTitle() {
        List<Attraction> sortedAttractions = controller.getAttractionsSortedByTitle();
        assertEquals(sortedAttractions.size(),8);
        assertEquals(sortedAttractions.get(0).name, "Angry Panda");
        assertEquals(sortedAttractions.get(7).name, "Zoo time");
    }

    /**
     * Test for getting Attractions after a given day. <br>
     * Size of the list is checked for different weekdays.
     */
    @Test
    void testGetAttractionsAfterAGivenDay(){
        assertEquals(controller.getAttractionsAfterAGivenDay(THURSDAY).size(), 4);
        List<Attraction> attractions = controller.getAttractionsAfterAGivenDay(SUNDAY);
        assertEquals(attractions.size(), 1);
        assertEquals(attractions.get(0), controller.getAllAttractions().get(6));
    }

    /**
     * Test for getting Guests of a given Attractions. <br>
     * <ul>
     *     <li> size of the list is checked for different Attractions </li>
     *     <li> verified if the list contains signed up guest </li>
     *     <li> verified if the list does not contain NOT signed up guest </li>
     * </ul>
     */
    @Test
    void testGetGuestsOfAttraction(){
        String idAttraction1 = controller.getAttractionsSortedByTitle().get(3).getID();
        List<Guest> guests = controller.getGuestsOfAttraction(idAttraction1);
        assertEquals(guests.size(), 10);
        Guest guest = controller.findGuestByUsername("leo_dicaprio");
        assertTrue(guests.contains(guest));
        guest = controller.findGuestByUsername("gibson_mel");
        assertFalse(guests.contains(guest));

        // no guests registered for the attraction (empty list)
        String idAttraction2 = controller.getAttractionsSortedByTitle().get(0).getID();
        assertEquals(controller.getGuestsOfAttraction(idAttraction2), new ArrayList<Guest>());
        assertFalse(controller.getGuestsOfAttraction(idAttraction2).contains(guest));
    }

    /**
     * Test for adding successfully a Guest (with unique username)
     */
    @Test
    void testSuccessfulAddGuest() {
        // add a new guest with unique username
        Guest guest1 = new Guest("ioana.petru2","Ioana", "Petru",  "123abc", LocalDate.of(1978,2,2));
        assertTrue(controller.addGuest(guest1));

        // add a new guest with same attributes as existing one (apart from username)
        Guest guest2 = new Guest("pop.otilia","Otilia", "Pop", "animals", LocalDate.of(2002,4,11));
        assertTrue(controller.addGuest(guest2));
    }

    /**
     * Test for adding unsuccessfully a Guest <br>
     * Causes:
     * <ul>
     *     <li> Username is already taken (not unique) </li>
     *     <li> The identified Guest is already registered </li>
     * </ul>
     */
    @Test
    void testUnSuccessfulAddGuest() {
        // add a new guest with existing username -> not possible
        Guest guest1 = new Guest("ioana.petru","Ioana", "Petru",  "123abc", LocalDate.of(1978,2,2));
        assertFalse(controller.addGuest(guest1));

        // add a guest twice -> not possible
        Guest guest2 = controller.findGuestByUsername("ioana.petru");
        assertNotNull(guest2);
        assertFalse(controller.addGuest(guest2));
    }

    /**
     * Test for adding successfully an Instructor (with unique username)
     */
    @Test
    void testSuccessfulAddInstructor() {
        // add instructor with unique id
        Instructor instructor1 = new Instructor("i10","Samantha", "Thompson", "passwordABC");
        assertTrue(controller.addInstructor(instructor1));
    }

    /**
     * Test for adding unsuccessfully an Instructor <br>
     * Causes:
     * <ul>
     *     <li> Username is already taken (not unique) </li>
     *     <li> The identified Instructor is already registered </li>
     * </ul>
     */
    @Test
    void testUnSuccessfulAddInstructor() {
        // add instructor with existing id -> not possible
        Instructor instructor2 = new Instructor("i4","John", "Simpson", "1234");
        assertFalse(controller.addInstructor(instructor2));

        // add instructor twice -> not possible
        Instructor instructor1 = controller.findInstructorByUsername("i3");
        assertNotNull(instructor1);
        assertFalse(controller.addInstructor(instructor1));
    }

    /**
     * Test for getAllGuests.
     */
    @Test
    void testGetAllGuests() {
        List<Guest> guests = this.controller.getAllGuests();
        assertEquals(guests.size(), 18);
        assertEquals(guests.get(0).getID(), "maria01");
        assertEquals(guests.get(17).getID(), "terence_hill");
    }

    /**
     * Test for getting Attractions of a given guest.
     * <ul>
     *     <li> size is verified </li>
     *     <li> verified if the list contains adequate attractions </li>
     *     <li> verified if the list does NOT contain inadequate attractions </li>
     *     <li> empty list for guest who is not signed up to any attractions </li>
     * </ul>
     */
    @Test
    void testGetAttractionsOfGuest() {
        // test with a guest who signed up for 2 attractions
        List<Attraction> guestAttractions = controller.getAttractionsOfGuest("maria01");
        assertEquals(guestAttractions.size(), 2);

        List<Attraction> attractions = controller.getAllAttractions();
        assertTrue(guestAttractions.contains(attractions.get(7)));
        assertTrue(guestAttractions.contains(attractions.get(4)));
        assertFalse(guestAttractions.contains(attractions.get(5)));

        // guest who is not registered to any attractions
        guestAttractions = controller.getAttractionsOfGuest("celined");
        assertEquals(guestAttractions.size(), 0);
        assertEquals(guestAttractions, new ArrayList<Attraction>());
    }

    /**
     * Test for getting the adequate final required sum from a Guest. <br>
     * Guests can receive ticket discounts depending on their age:
     * <ul>
     *     <li> under 18 -> 50% discount </li>
     *     <li> above 60 -> 20% discount </li>
     * </ul>
     * <br> After signing up to an Attraction the Guest's final sum increases.
     * <br> After cancelling an Attraction the Guest's final sum decreases.
     */
    @Test
    void testGetFinalSumOfGuest() throws NoMoreAvailableTicketsException{
        // under 18 -> ticket with discount
        assertEquals(this.controller.getFinalSumOfGuest("katy99"),150.435);
        // above 60 -> ticket with discount
        assertEquals(this.controller.getFinalSumOfGuest("leo_dicaprio"),240.696, 0.1);

        assertEquals(this.controller.getFinalSumOfGuest("maria01"),550.87);
        Attraction attraction = this.controller.getAllAttractions().get(0);
        String idAttraction = this.controller.getAllAttractions().get(0).getID();
        Instructor instructor = attraction.getInstructor();
        double sumInstructor = instructor.getFinalSum();
        assertEquals(sumInstructor,0);
        assertEquals(attraction.getNrOfGuests(),0);

        // sign up for attraction -> sum is increased
        assertTrue(this.controller.signUpForAttraction("maria01", idAttraction));
        assertEquals(this.controller.getFinalSumOfGuest("maria01"),731.86);
        // instructor gets more money
        assertEquals(instructor.getFinalSum(), sumInstructor + attraction.price);

        // attraction deleted -> sum is decreased (at guest and instructor too)
        this.controller.deleteAttraction("i1",idAttraction);
        assertEquals(this.controller.getFinalSumOfGuest("maria01"),550.87);
        assertEquals(instructor.getFinalSum(), sumInstructor);
    }

    /**
     * Test for getting the adequate final sum of an Instructor (income from Guests). <br>
     */
    @Test
    void testGetSumFromGuests() {
        // instructor who hold attractions where are yet no guests signed up
        assertEquals(this.controller.getSumFromGuests("i2"),0);
        // instructor who holds more attractions with more Guests having discounts
        assertEquals(this.controller.getSumFromGuests("i6"),2647.65,0.1);
    }

    /**
     * Test for getting the income of the Zoo. <br>
     */
    @Test
    void getIncomeOfTheZoo() {
        assertEquals(this.controller.getIncomeOfTheZoo(),3647.65,0.1);
    }

    /**
     * Test for getting Attractions sorted descending by sum.
     */
    @Test
    void testGetGuestsSortedDescendingBySum() {
        List<Guest> guests = this.controller.getGuestsSortedDescendingBySum();
        assertEquals(guests.size(),18);
        System.out.println(guests.get(0).getFinalSum());
        assertEquals(guests.get(0).getFinalSum(), 550.87);
        assertEquals(guests.get(7).getFinalSum(),250);
        assertEquals(guests.get(17).getFinalSum(), 0.0);
    }

    /**
     * Test for getting Instructors.
     */
    @Test
    void testGetAllInstructors() {
        List<Instructor> instructors = controller.getAllInstructors();
        assertEquals(instructors.size(), 6);
        assertEquals(instructors.get(0).getID(), "i1");
        assertEquals(instructors.get(5).getID(), "i6");
    }

    /**
     * Test for identifying an Instructor by username.
     */
    @Test
    void testFindInstructorByUsername(){
        Instructor instructor = this.controller.findInstructorByUsername("i2");
        assertEquals(instructor, controller.getAllInstructors().get(1));
        assertEquals(instructor.getName(), "James John");
        assertEquals(instructor.getPassword(), "qwerty");

        // find instructor with non-existent id
        instructor = this.controller.findInstructorByUsername("i10");
        assertNull(instructor);
    }

    /**
     * Test for identifying a Guest by username.
     */
    @Test
    void testFindGuestByUsername(){
        Guest guest = this.controller.findGuestByUsername("maria01");
        assertEquals(guest, controller.getAllGuests().get(0));
        assertEquals(guest.getName(), "Maria Kis");
        assertEquals(guest.getPassword(), "KM01");

        // find guest with non-existent id
        guest = this.controller.findGuestByUsername("janet_j");
        assertNull(guest);
    }

    /**
     * Testing the change of an instructor. <br>
     * Verify if in the Attraction-list of the old Instructor the Attraction disappeared and in
     * the new Instructors' Attraction-list appeared.
     */
    @Test
    void testChangeInstructorOfAttraction(){
        Instructor instructor1 = this.controller.findInstructorByUsername("i6");
        Attraction attraction = attractionRepository.getAllAttractions().get(7);
        // in the attraction-list of the instructor appears the attraction and vice versa
        assertEquals(instructor1, attraction.getInstructor());
        assertTrue(instructor1.getAttractions().contains(attraction));

        boolean succesful = this.controller.changeInstructorOfAttraction(attraction.getID(),"i1");
        assertTrue(succesful);
        Instructor instructor2 = this.controller.findInstructorByUsername("i1");
        // in the attraction-list of the instructor the attraction disappeared
        assertFalse(instructor1.getAttractions().contains(attraction));

        // at the same position we have a new instructor for the attraction
        assertEquals(attractionRepository.getAllAttractions().get(7).getInstructor(), instructor2);
        // changes must appear at the side of instructors as well
        assertEquals(instructor2.getAttractions().get(1), attraction);

        // changes must be seen at guests too
        Guest guest = this.controller.findGuestByUsername("maria01");
        assertTrue(attraction.guestList.contains(guest));
        assertEquals(guest.getAttractions().get(0).getInstructor(), instructor2);

        // changing instructor to a new instructor with non-existent id -> not possible
        succesful = this.controller.changeInstructorOfAttraction(attraction.getID(),"i20");
        assertFalse(succesful);
    }

    /**
     * Testing the sign-up for an Attraction for a Guest who was already signed up.
     */
    @Test
    void testUnsuccesfulSignUpForAlreadySignedUpGuest(){
        // guest already signed up -> sign-up again not possible
        Guest guest1 = this.controller.findGuestByUsername("maria01");
        Attraction attraction1 = this.controller.getAllAttractions().get(4);
        assertTrue(guest1.getAttractions().contains(attraction1));
        assertTrue(attraction1.guestList.contains(guest1));
        boolean successful = this.controller.signUpForAttraction("maria01", attraction1.getID());
        assertFalse(successful);
    }

    /**
     * Testing the sign-up for an Attraction with no available places.
     */
    @Test
    void testUnSuccessfulSignUpForAttractionWithNoFreePlaces(){
        // sign up when there are no free places -> not possible
        Attraction attraction2 = this.controller.getAttractionsSortedByTitle().get(3);
        assertEquals(attraction2.getNrOfFreePlaces(), 0);

        assertFalse(this.controller.signUpForAttraction("ioana_maria", attraction2.getID()));
    }

    /**
     * Test for the successful sign-up for an Attraction.
     */
    @Test
    void testSuccessfulSignUpForAttraction() {
        Attraction attraction = this.controller.getAllAttractions().get(4);
        // successful sign up -> number of free places decreases, sum paid by guests increases
        Guest guest = new Guest("ioana_maria","Ioana", "Maria", "passw123", LocalDate.of(1970,8,10));
        this.controller.addGuest(guest);
        assertEquals(attraction.getNrOfFreePlaces(), 4);
        boolean successful = this.controller.signUpForAttraction("ioana_maria", attraction.getID());
        assertTrue(successful);
        assertEquals(attraction.getNrOfFreePlaces(),3);
        assertEquals(this.controller.getFinalSumOfGuest("ioana_maria"), attraction.price);
    }

    /**
     * Test for unsuccessfully cancelling an Attraction. When the Instructor who initiates the cancel is not the Instructor of the Attraction.
     */
    @Test
    void testUnsuccessfulDeleteAttraction(){
        assertEquals(this.controller.getAllAttractions().size(), 8);
        Attraction attraction = this.controller.getAttractionsSortedByTitle().get(3);

        // delete attraction by another instructor than who holds the attraction -> not possible
        boolean successful = this.controller.deleteAttraction("i3", attraction.getID());
        assertFalse(successful);
        assertEquals(this.controller.getAllAttractions().size(), 8);
    }

    /**
     * Test for successfully cancelling an Attraction.
     */
    @Test
    void testDeleteAttraction() {
        assertEquals(this.controller.getAllAttractions().size(), 8);
        Attraction attraction = this.controller.getAttractionsSortedByTitle().get(3);

        // delete attraction by own instructor -> changes appear at the guests too
        Instructor instructor = this.controller.findInstructorByUsername("i6");
        assertTrue(instructor.getAttractions().contains(attraction));
        boolean successful = this.controller.deleteAttraction("i6", attraction.getID());
        assertTrue(successful);
        for (Guest guest: attraction.guestList){
            assertFalse(guest.getAttractions().contains(attraction));
        }
        assertFalse(instructor.getAttractions().contains(attraction));
        assertEquals(this.controller.getAllAttractions().size(), 7);
    }

    /**
     * Test for getting Attractions sorted ascending by price.
     */
    @Test
    void testGetAttractionsSortedByPriceAscending() {
        List<Attraction> sortedAttractionsByPrice = controller.getAttractionsSortedByPriceAscending();
        assertEquals(sortedAttractionsByPrice.get(0).price,55.00);
        assertEquals(sortedAttractionsByPrice.get(7).price,300.87);
    }

    /**
     * Test for getting Attractions sorted by Guest number.
     */
    @Test
    void testGetAttractionsSortedByGuestAscending() {
        List<Attraction> sortedAttractionsByNrOfGuests = controller.getAttractionsSortedByGuestAscending();
        assertEquals(sortedAttractionsByNrOfGuests.get(7).getNrOfGuests(), 10);
        assertEquals(sortedAttractionsByNrOfGuests.get(0).getNrOfGuests(), 0);
    }

    /**
     * Test for getting Attractions with smaller price as a given value.
     */
    @Test
    void testFilterAttractionsByAGivenValue() {
        List<Attraction> sortedAttractions = controller.filterAttractionsByAGivenValue(99.99);
        assertEquals(sortedAttractions.size(),4);
    }

    /**
     * Test for getting Attractions with smaller price as a given value - when there are no matching Attractions.
     */
    @Test
    void testNotFoundDataFilterAttractionsByAGivenValue() {
        // no matching attractions
        List<Attraction> attrWithPriceLessThan10 = this.controller.filterAttractionsByAGivenValue(10);
        assertEquals(attrWithPriceLessThan10.size(),0);
    }

    /**
     * Test for getting the average income of an Instructor.
     */
    @Test
    void testGetAverageSalaryOfInstructors() {
        assertEquals(controller.getAverageSalaryOfInstructors(),607.94,0.1);
    }

    /**
     * Test for getting the Instructors with higher income as the average.
     */
    @Test
    void testFilterInstructorsWithHigherSalaryThanAverage() {
        List<Instructor> instructorsWithHighSalary = controller.filterInstructorsWithHigherSalaryThanAverage();
        assertEquals(instructorsWithHighSalary.size(),2);

        // both instructors have higher salary than the average (668.116)
        Instructor instructor = instructorsWithHighSalary.get(0);
        assertEquals(instructor.getFinalSum(), 1000);

        instructor = instructorsWithHighSalary.get(1);
        assertEquals(instructor.getFinalSum(), 2647.65,0.1);

        // any other instructor have lower salary
        instructor = controller.getAllInstructors().get(2);
        assertFalse(instructorsWithHighSalary.contains(instructor));
        assertEquals(instructor.getFinalSum(), 0);
    }

    /**
     * Test for incorrect user input regarding name and password.
     * <ul>
     *     <li> firstname and lastname containing not only letters </li>
     *     <li> too short password (less than 3 characters) </li>
     * </ul>
     */
    @Test
    void testVerifyIncorrectUserInputNameAndPassword() {
        // firstname and lastname containing not only letters -> incorrect input
        assertFalse(controller.verifyUserInputNameAndPassword("samantha1","Samantha1", "Baker", "12345"));
        assertFalse(controller.verifyUserInputNameAndPassword("samantha2","Samantha", "Baker1", "12345"));
        // too short password (less than 3 characters) -> incorrect input
        assertFalse(controller.verifyUserInputNameAndPassword("samantha2","Samantha", "Baker", "ab"));
    }

    /**
     * Test for correct user input regarding name and password.
     */
    @Test
    void testVerifyCorrectUserInputNameAndPassword() {
        assertTrue(controller.verifyUserInputNameAndPassword("samantha2","Samantha", "Baker", "abcd"));
    }

    /**
     * Test for incorrect user input. <br>
     * If any of the following appears:
     * <ul>
     *     <li> incorrect username (containing uppercase letters too) </li>
     *     <li> incorrect name (containing not only letters) </li>
     *     <li> incorrect birthday format (not YEAR-MONTH-DAY) </li>
     *     <li> incorrect password (less than 3 characters)</li>
     * </ul>
     */
    @Test
    void testVerifiedIncorrectUserInputGuest() {
        // incorrect username (containing uppercase letters too)
        assertNull(controller.verifiedUserInputGuest("SAMANTHA2","Samantha","Baker","2000-10-10", "abcd"));
        // incorrect name (containing not only letters)
        assertNull(controller.verifiedUserInputGuest("samantha2","Samantha12","Baker34","2000-10-10", "abcd"));
        // incorrect birthday format (not YEAR-MONTH-DAY)
        assertNull(controller.verifiedUserInputGuest("samantha2","Samantha","Baker","2000.10.10", "abcd"));
        // incorrect password (less than 3 characters)
        assertNull(controller.verifiedUserInputGuest("samantha2","Samantha","Baker","2000-10-10", "12"));
        // all given data incorrect
        assertNull(controller.verifiedUserInputGuest("maria01","Maria*@","Baker12","2000-30-10", "abcd123"));
    }

    /**
     * Test for incorrect user input regarding Weekday. (not Enum-element)
     */
    @Test
    void testVerifiedIncorrectUserInputWeekday() {
        assertNull(controller.verifiedUserInputWeekday("Donnerstag"));
    }

    /**
     * Test for correct user input regarding Weekday. (Enum-element)
     */
    @Test
    void testVerifiedCorrectUserInputWeekday() {
        assertInstanceOf(Weekday.class,(controller.verifiedUserInputWeekday("Monday")));
        // small letters are also allowed
        assertInstanceOf(Weekday.class,(controller.verifiedUserInputWeekday("monday")));
    }

    /**
     * Test for incorrect user input regarding price.
     */
    @Test
    void testVerifiedIncorrectUserInputPrice() {
        assertEquals(controller.verifiedUserInputPrice("23,4"),0.0);
        assertEquals(controller.verifiedUserInputPrice("one hundred"),0.0);
    }

    /**
     * Test for correct user input regarding price.
     */
    @Test
    void testVerifiedCorrectUserInputPrice() {
        assertEquals(controller.verifiedUserInputPrice("23.4"),23.4);
    }

    /**
     * Test for incorrect user input regarding Attraction. <br>
     * If any of the following appears:
     * <ul>
     *     <li> incorrect capacity type </li>
     *     <li> incorrect day type(nut Enum-element) </li>
     *     <li> incorrect price type </li>
     * </ul>
     */
    @Test
    void testVerifiedIncorrectUserInputAttraction() {
        // capacity incorrect
        assertNull(controller.verifiedUserInputAttraction("Birds show", "50.5", "100", "Main1", "Monday"));
        // weekday incorrect
        assertNull(controller.verifiedUserInputAttraction("Birds show", "50", "100", "Main1", "Donnerstag"));
        // price incorrect
        assertNull(controller.verifiedUserInputAttraction("Birds show", "50", "one hundred", "Main1", "Monday"));
    }

    /**
     * Test for correct user input regarding Attraction.
     */
    @Test
    void testVerifiedCorrectUserInputAttraction() {
        Attraction attraction = controller.verifiedUserInputAttraction("Birds show", "50", "100.3", "Main1", "Monday");
        assertEquals(attraction.name, "Birds show");
        assertEquals(attraction.getCapacity(), 50);
        assertEquals(attraction.price, 100.3);
        assertEquals(attraction.day.getNr(), 1);
    }
}