import registration.RegistrationSystem;
import repository.AttractionRepository;
import repository.GuestRepository;
import repository.InstructorRepository;
import repository.jdbcRepository.JdbcAttractionRepository;
import repository.jdbcRepository.JdbcGuestRepository;
import repository.jdbcRepository.JdbcInstructorRepository;
import repository.memoryRepo.InMemoryAttractionRepository;
import repository.memoryRepo.InMemoryGuestRepository;
import repository.memoryRepo.InMemoryInstructorRepository;
import ui.UI;

public class Main {
    public static void main(String[] args) {
        // in memory
        InstructorRepository instructorRepository = new InMemoryInstructorRepository();
        AttractionRepository attractionRepository = new InMemoryAttractionRepository(instructorRepository);
        GuestRepository guestRepository = new InMemoryGuestRepository(attractionRepository);

        RegistrationSystem controller = new RegistrationSystem(attractionRepository, guestRepository, instructorRepository);
        UI ui = new UI(controller);
        ui.getUserChoice();

        // database
//        JdbcInstructorRepository jdbcInstructorRepository = new JdbcInstructorRepository("default");
//        JdbcAttractionRepository jdbcAttractionRepository = new JdbcAttractionRepository(jdbcInstructorRepository);
//        GuestRepository jdbcGuestRepository = new JdbcGuestRepository(jdbcAttractionRepository);
//
//        RegistrationSystem jdbcController = new RegistrationSystem(jdbcAttractionRepository, jdbcGuestRepository, jdbcInstructorRepository);
//        UI jdbcUi = new UI(jdbcController);
//        jdbcUi.getUserChoice();
    }
}