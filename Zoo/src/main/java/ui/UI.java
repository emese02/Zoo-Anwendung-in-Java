package ui;

import domain.Attraction;
import domain.Guest;
import domain.Instructor;
import domain.Weekday;
import registration.RegistrationSystem;
import utils.NoMoreAvailableTicketsException;
import utils.NoSuchDataException;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * UI (View) - visualization of data, calling the methods of the controller.
 */
public class UI {
    /**
     * Controller, whose methods are called.
     */
    private final RegistrationSystem controller;

    /**
     * Constructor - constructs and initializes UI. <br>
     * @param controller Controller whose methods are called
     */
    public UI(RegistrationSystem controller) {
        this.controller = controller;
    }

    /**
     * Shows registration-menu with options.
     */
    public void showMenuRegistration() {
        System.out.println("""
                
                1. Registration
                2. Schon registriert
                """);
    }

    /**
     * Shows Guest-menu with options - the functionalities which are available for the Guest.
     */
    public void showMenuGuest(){
        System.out.println("""
                
                1. Zeige alle Attraktionen
                2. Zeige alle verfügbare Attraktionen
                3. Zeigen Attraktionen nach einem bestimmten Tag
                4. Für eine Attraktion anmelden
                5. Attraktionen, für denen Sie angemeldet sind
                6. Zeige die bezahlende Endsumme
                7. Zeige Attraktionen nach Preis sortiert
                8. Zeige Attraktionen mit einem kleinern Preis als ein gegebener Wert
                9. Exit
                """);
    }

    /**
     * Shows Instructor-menu with options - the functionalities which are available for the Instructor.
     */
    public void showMenuInstructor(){
        System.out.println("""
                
                1. Zeige gehältende Attraktionen
                2. Neue Attraktion einfügen
                3. Attraktion absagen
                4. Zeige Summe von Besucher
                5. Exit
                """);
    }

    /**
     * Shows Manager-menu with options - the functionalities which are available for the Manager - statistical data.
     */
    public void showMenuManager(){
        System.out.println("""
                
                1. Zeige alle Attraktionen
                2. Zeige alle Instruktoren
                3. Zeige alle Besucher
                4. Zeige alle Besucher absteigend sortiert nach der bezahlten Gesamtsumme
                5. Zeige Besucher einer Attraktion
                6. neuen Instruktor für eine Attraktion auswählen
                7. Attraktionen nach BesucherAnzahl sortiert
                8. Zeige Instruktoren mit höherem Einkommen als der Durchschnitt
                9. Zeige durschnittliche Einkommen der Instruktoren
                10. Die Einkünfte des Zoos 
                11. Exit
                """);
    }

    /**
     * Shows Menu ->  Instructor - Guest - Manager
     */
    // menu Instructor - Guest - Manager
    public void showMenuIGM(){
        System.out.println("""
                
                1. Instruktor
                2. Besucher
                3. Manager
                """);
    }

    /**
     * Reads in input and shows menu points and results respectively.
     */
    public void getUserChoice(){
        System.out.println("Wähle eine Option: ");
        this.showMenuIGM();
        Scanner in = new Scanner(System.in);
        int choice = in.nextInt(), choiceMenu = 0, count;
        boolean successful;
        double value;
        String idGuest = null, idAttraction = null, idInstructor, emptyLine, username = null, password;
        while (choice == 1 || choice == 2 || choice == 3) {
            if (choice == 1) {
                successful = false;
                while (!successful) {
                    showMenuRegistration();
                    choiceMenu = in.nextInt();
                    switch (choiceMenu) {
                        case 1:
                            // registration
                            Instructor instructor = readInInstructor();
                            successful = this.controller.addInstructor(instructor);
                            if (successful) {
                                System.out.println("Registration erfolgreich!\n");
                                username = instructor.getID();
                            } else
                                System.out.println("Registration nicht möglich!\n");
                            break;
                        case 2:
                            // authentification
                            System.out.print("Gib deinen Username an: ");
                            emptyLine = in.nextLine();
                            username = in.nextLine();
                            System.out.print("Gib dein Passwort an: ");
                            password = in.nextLine();
                            Instructor instr = this.controller.findInstructorByUsername(username);
                            if (instr != null) {
                                count = 0;
                                while (!instr.matchesPassword(password) && count != 3) {
                                    System.out.print("Falsches Passwort, versuche es wieder: ");
                                    password = in.nextLine();
                                    count++;
                                }
                                if (count != 3){
                                    successful = true;
                                    System.out.println("Das Passwort passt!");
                                }
                                else System.out.println("Die gegebenen Passwörter waren falsch!");
                            } else
                                System.out.println("Es gibt keinen Instruktor der sich mit diesem Usernamen registriert hat.");
                            break;
                        default:
                            System.out.println("Es gibt so eine Option nicht");
                            break;
                    }
                }
                showMenuInstructor();
                choiceMenu = in.nextInt();
                while (choiceMenu != 5) {
                    switch (choiceMenu) {
                        case 1:
                            System.out.println(this.controller.getAttractionsOfInstructor(username));
                            break;
                        case 2:
                            Attraction attraction = readInAttraction();
                            successful = this.controller.addAttraction(attraction, username);
                            if (successful)
                                System.out.println("Attraktion eingefügt");
                            else
                                System.out.println("Attraktion ist nicht eingefügt");
                            break;
                        case 3:
                            List<Attraction> attractions = this.controller.getAttractionsOfInstructor(username);
                            if (attractions.size() > 0) {
                                System.out.println(attractions);
                                System.out.println("Gib ID-Attraktion an: ");
                                emptyLine = in.nextLine();
                                idAttraction = in.nextLine();
                                successful = this.controller.deleteAttraction(username, idAttraction);
                                if (successful)
                                    System.out.println("Attraktion ist abgesagt");
                                else
                                    System.out.println("Prozess fehlgeschlafen");
                            }
                            break;
                        case 4:
                            System.out.println("Summe: " + this.controller.getSumFromGuests(username));
                            break;
                        case 5:
                            break;
                        default:
                            System.out.println("So eine Option exisitiert nicht!\n");
                    }
                    showMenuInstructor();
                    choiceMenu = in.nextInt();
                }
            }
            else if (choice == 2) {
                    successful = false;
                    while (!successful) {
                        showMenuRegistration();
                        choiceMenu = in.nextInt();
                        switch (choiceMenu) {
                            case 1:
                                // registration
                                Guest guest = readInGuest();
                                successful = this.controller.addGuest(guest);
                                if (successful) {
                                    System.out.println("Registration erfolgreich!\n");
                                    username = guest.getID();
                                } else
                                    System.out.println("Registration nicht möglich!\n");
                                break;
                            case 2:
                                // authentification
                                System.out.print("Gib deinen Username an: ");
                                emptyLine = in.nextLine();
                                username = in.nextLine();
                                System.out.print("Gib dein Passwort an: ");
                                password = in.nextLine();
                                Guest g = this.controller.findGuestByUsername(username);
                                if (g != null) {
                                    count = 0;
                                    while (!g.matchesPassword(password) && count != 3) {
                                        System.out.print("Falsches Passwort, versuche es wieder: ");
                                        password = in.nextLine();
                                        count++;
                                    }
                                    if (count != 3){
                                        successful = true;
                                        System.out.println("Das Passwort passt!");
                                    }
                                    else System.out.println("Die eingegebenen Passwörter waren falsch!");
                                } else
                                    System.out.println("Es gibt keinen Benutzer der sich mit diesem Usernamen registriert hat.");
                                break;
                            default:
                                System.out.println("Es gibt so eine Option nicht");
                                break;
                        }
                    }
                    showMenuGuest();
                    choiceMenu = in.nextInt();
                    while (choiceMenu != 9) {
                        switch (choiceMenu) {
                            case 1:
                                System.out.println(this.controller.getAttractionsSortedByTitle());
                                break;
                            case 2:
                                System.out.println(this.controller.getAllAttractionsWithFreePlaces());
                                break;
                            case 3:
                                System.out.println("Gib einen Tag an: ");
                                emptyLine = in.nextLine();
                                Weekday day = this.controller.verifiedUserInputWeekday(in.nextLine());
                                System.out.println(this.controller.getAttractionsAfterAGivenDay(day));
                                break;
                            case 4:
                                System.out.println(this.controller.getAttractionsSortedByTitle());
                                System.out.println("Gib eine Attraktion ID an: ");
                                emptyLine = in.nextLine();
                                idAttraction = in.nextLine();
                                successful = this.controller.signUpForAttraction(username, idAttraction);
                                if (successful)
                                    System.out.println("Anmeldung erfolgreich!\n");
                                else
                                    System.out.println("Anmeldung nicht möglich!\n");
                                break;
                            case 5:
                                System.out.println(this.controller.getAttractionsOfGuest(username));
                                break;
                            case 6:
                                System.out.println("Die bezahlende Endsumme: " + this.controller.getFinalSumOfGuest(username));
                                break;
                            case 7:
                                System.out.println(this.controller.getAttractionsSortedByPriceAscending());
                                break;
                            case 8:
                                System.out.print("Gib den maximum Preis: ");
                                emptyLine = in.nextLine();
                                value = this.controller.verifiedUserInputPrice(in.nextLine());
                                System.out.println(this.controller.filterAttractionsByAGivenValue(value));
                                break;
                            case 9:
                                break;
                            default:
                                System.out.println("So eine Option exisitiert nicht!\n");
                        }
                        showMenuGuest();
                        choiceMenu = in.nextInt();
                    }
                } else {
                    showMenuManager();
                    choiceMenu = in.nextInt();
                    while (choiceMenu != 11) {
                        switch (choiceMenu) {
                            case 1:
                                System.out.println(this.controller.getAllAttractions());
                                break;
                            case 2:
                                System.out.println(this.controller.getAllInstructors());
                                break;
                            case 3:
                                this.showGuestData(this.controller.getAllGuests());
                                break;
                            case 4:
                                System.out.println(this.controller.getGuestsSortedDescendingBySum());
                                break;
                            case 5:
                                System.out.println(this.controller.getAllAttractions());
                                System.out.println("Wähle eine Attraktion-ID: ");
                                emptyLine = in.nextLine();
                                idAttraction = in.nextLine();
                                System.out.println("ID: " + idAttraction);
                                List<Guest> guests = this.controller.getGuestsOfAttraction(idAttraction);
                                if (guests != null)
                                    System.out.println(guests);
                                else
                                    System.out.println("Es gibt noch keine angemeldete Besuchern");
                                break;
                            case 6:
                                System.out.println(this.controller.getAllAttractions());
                                System.out.println("Wähle eine Attraktion-ID: ");
                                emptyLine = in.nextLine();
                                idAttraction = in.nextLine();
                                System.out.println("Wähle einen neuen Instruktor: ");
                                System.out.println(this.controller.getAllInstructors());
                                System.out.println("Gib ID an: ");
                                idInstructor = in.nextLine();
                                successful = this.controller.changeInstructorOfAttraction(idAttraction,idInstructor);
                                if (successful) {
                                    System.out.println("Veränderungen gespeichert!\n");
                                } else
                                    System.out.println("Prozess fehlgeschlagen\n");
                                break;
                            case 7:
                                System.out.println(this.controller.getAttractionsSortedByGuestAscending());
                                break;
                            case 8:
                                System.out.println(this.controller.filterInstructorsWithHigherSalaryThanAverage());
                                break;
                            case 9:
                                System.out.printf("%s %.2f%n", "Durschnittliche Einkommen der Instruktoren: ", this.controller.getAverageSalaryOfInstructors());
                                break;
                            case 10:
                                System.out.printf("%s %.2f%n", "Die Einkünfte des Zoos sind: ", this.controller.getIncomeOfTheZoo());
                                break;
                            case 11:
                                break;
                            default:
                                System.out.println("Es gibt so eine Option nicht");
                                break;
                        }
                        showMenuManager();
                        choiceMenu = in.nextInt();
                    }
                }
                System.out.println("Wähle eine Option: ");
                showMenuIGM();
                choice = in.nextInt();
            }
        }

    /**
     * Reads in input for a new Guest, which data will be verified in Controller.
     * @return new Guest - if the input is correct, false otherwise
     */
    public Guest readInGuest(){
        Scanner in = new Scanner(System.in);
        System.out.print("Gib deinen Username an: ");
        String username = in.nextLine();
        System.out.print("Gib deinen Vorname an: ");
        String firstName = in.nextLine();
        System.out.print("Gib deinen Nachname an: ");
        String lastName = in.nextLine();
        System.out.print("Gib dein Geburtsdatum an: ");
        String birthday = in.nextLine();
        System.out.print("Gib dein Passwort an: ");
        String password = in.nextLine();
        Guest guest = this.controller.verifiedUserInputGuest(username, firstName, lastName, birthday, password);
        return guest;
    }

    /**
     * Reads in input for a new Instructor, which data will be verified in Controller.
     * @return new Instructor - if the input is correct, false otherwise
     */
    public Instructor readInInstructor() {
        Scanner in = new Scanner(System.in);
        System.out.print("Gib deinen Username an: ");
        String username = in.nextLine();
        System.out.print("Gib deinen Vorname an: ");
        String firstName = in.nextLine();
        System.out.print("Gib deinen Nachname an: ");
        String lastName = in.nextLine();
        System.out.print("Gib dein Passwort an: ");
        String password = in.nextLine();
        boolean correctInput = this.controller.verifyUserInputNameAndPassword(username,firstName,lastName,password);
        if (correctInput)
            return new Instructor(username, firstName, lastName, password);
        return null;
    }

    /**
     * Reads in input for a new Attraction, which data will be verified in Controller.
     * @return new Attraction - if the input is correct, false otherwise
     */
    public Attraction readInAttraction() {
        Scanner in = new Scanner(System.in);
        System.out.print("Gib der Name der Attraktion an: ");
        String name = in.nextLine();
        System.out.print("Gib die Kapazitaet des Ortes für diese Attraktion an: ");
        String capacity = in.nextLine();
        System.out.print("Gib den Preis dieser Attraktion an: ");
        String price = in.nextLine();
        System.out.print("Gib die Stätte an: ");
        String location = in.nextLine();
        System.out.print("Gib den Tag an: ");
        String weekday = in.nextLine();
        return this.controller.verifiedUserInputAttraction(name, capacity, price, location, weekday);
    }

    /**
     * The method shows specific data about the Guests in a list received as a parameter. <br>
     * It uses the getData() method of the class Guest.
     * @param guests list of Guests about which we want to see specific data.
     */
    public void showGuestData(List<Guest> guests){
        for (Guest g: guests){
            System.out.println(g.getData());
        }

    }
}
