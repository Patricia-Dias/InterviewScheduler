package com.example.interviewscheduler.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InterviewSlotRepositoryTest {
    
    @Container
	public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:12")
		.withUsername("xgeeks")
		.withPassword("password")
		.withDatabaseName("scheduler");

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.password", container::getPassword);
		registry.add("spring.datasource.username", container::getUsername);
	}

    @Autowired
    private InterviewSlotRepository interviewSlotRepository;

    @Autowired
    private TestEntityManager entityManager;

    private InterviewSlot interviewSlot1;

    @AfterEach
    void resetDB(){
        interviewSlotRepository.deleteAll();
        entityManager.clear();
    }

    @BeforeEach
    void setUp(){
        interviewSlot1 = createAndSaveInterviewSlot();
    }

    /* Saving Interview Slot */

    @Test
    void whenInterviewSlotSaved_findByCorrectIdShouldReturnCorrectInterviewSlot(){

        Optional<InterviewSlot> optionalInterviewSlotFromDB = interviewSlotRepository.findById(interviewSlot1.getId());
        assertThat(optionalInterviewSlotFromDB).isPresent();

        InterviewSlot interviewSlotFromDB = optionalInterviewSlotFromDB.get();
        assertThat(interviewSlotFromDB.getInterviewer()).isEqualTo(interviewSlot1.getInterviewer());
        assertThat(interviewSlotFromDB.getTime()).isEqualTo(interviewSlot1.getTime());
        assertThat(interviewSlotFromDB.getCandidate()).isEqualTo(interviewSlot1.getCandidate());
        assertThat(interviewSlotFromDB.getId()).isEqualTo(interviewSlot1.getId());

    }

    @Test
    void whenInterviewSlotSaved_findByInvalidIdShouldReturnEmptyOptionalObject(){        
        Optional<InterviewSlot> optionalInterviewSlotFromDB = interviewSlotRepository.findById(-1L);
        assertThat(optionalInterviewSlotFromDB).isEmpty();

    }

    /* Find By Time And Interviewer */

    @Test
    void whenValidTimeAndInterviewer_findByTimeAndInterviewerShouldReturnInterviewSlot(){
        
        Optional<InterviewSlot> optionalInterviewSlotFromDB = interviewSlotRepository
            .findByTimeAndInterviewer(interviewSlot1.getTime(), interviewSlot1.getInterviewer());
        
        assertThat(optionalInterviewSlotFromDB).isPresent();
        assertThat(optionalInterviewSlotFromDB.get()).isEqualTo(interviewSlot1);
    }

    @Test
    void whenInvalidTimeAndInterviewer_findByTimeAndInterviewerShouldReturnEmptyOptionalObject(){  
        createAndSaveInterviewer(1L);

        Optional<InterviewSlot> optionalInterviewSlotFromDB = interviewSlotRepository
            .findByTimeAndInterviewer(LocalDateTime.now(), createAndSaveInterviewer(2L)); // testing interviewer, 
                                    // to check if repository  returns null when there's not an interviewer 
                                    // instance with ID = 2L saved in the database
        
        
        assertThat(optionalInterviewSlotFromDB).isEmpty();
    }

    /* Find Available Slots */
    @Test
    void findAvailableSlotsShouldReturnListOfInterviewSlots(){
        InterviewSlot interviewSlot2 = createAndSaveInterviewSlot();
        List<InterviewSlot> listOfSlots = List.of(interviewSlot1, interviewSlot2);
        
        List<InterviewSlot> listInterviewSlotFromDB = interviewSlotRepository
            .findAvailableSlots();
        
        assertThat(listInterviewSlotFromDB).hasSameSizeAs(listOfSlots)
            .isEqualTo(listOfSlots);
        for (int i=0; i<listInterviewSlotFromDB.size(); i++){
            assertThat(listInterviewSlotFromDB.get(i).getCandidate()).isNull();
        }
    }

    /* Find Available Slots by Time */
    @Test
    void findAvailableSlotsByTimeShouldReturnListOfInterviewSlots(){
        
        List<InterviewSlot> listInterviewSlotFromDB = interviewSlotRepository
            .findAvailableSlotsByTime(interviewSlot1.getTime());
        
        assertThat(listInterviewSlotFromDB).hasSameSizeAs(List.of(interviewSlot1))
            .isEqualTo(List.of(interviewSlot1));
        assertThat(listInterviewSlotFromDB.get(0).getTime()).isEqualTo(interviewSlot1.getTime());
    }

    /* -- HELPERS -- */
    private InterviewSlot createAndSaveInterviewSlot() {
        InterviewSlot interviewSlot = new InterviewSlot();
        Interviewer interviewer = createAndSaveInterviewer(1L);

        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(LocalDateTime.parse("2042-08-30T10:00:00"));

        entityManager.persistAndFlush(interviewSlot);

        return interviewSlot;
    }

    private Interviewer createAndSaveInterviewer(long id){

        Interviewer interviewer = createInterviewer(id);
        entityManager.persistAndFlush(interviewer);

        return interviewer;
    }

    private Interviewer createInterviewer(long id){
        Interviewer interviewer = new Interviewer();

        interviewer.setName("Carlos");
        interviewer.setEmail("carlos"+id+"@gmail.com");

        return interviewer;
    }

    

}
