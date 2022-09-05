package com.example.interviewscheduler.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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

import com.example.interviewscheduler.models.Interviewer;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InterviewerRepositoryTest {
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
    private InterviewerRepository interviewerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void resetDB(){
        interviewerRepository.deleteAll();
        entityManager.clear();
    }

    @Test
    void whenInterviewerSaved_findByCorrectEmailShouldReturnCorrectInterviewer(){
        Interviewer interviewer1 = createAndSaveInterviewer(1L);
        
        Optional<Interviewer> optionalInterviewerFromDB = interviewerRepository.findByEmail(interviewer1.getEmail());
        assertThat(optionalInterviewerFromDB).isPresent();

        Interviewer interviewerFromDB = optionalInterviewerFromDB.get();
        assertThat(interviewerFromDB.getEmail()).isEqualTo(interviewer1.getEmail());
        assertThat(interviewerFromDB.getName()).isEqualTo(interviewer1.getName());
        assertThat(interviewerFromDB.getId()).isEqualTo(interviewer1.getId());
    }

    @Test
    void whenInterviewerSaved_findByIncorrectEmailShouldReturnEmptyOptionalObject(){
        createAndSaveInterviewer(1L);
        
        Optional<Interviewer> optionalInterviewerFromDB = interviewerRepository.findByEmail("invalid@email.com");
        assertThat(optionalInterviewerFromDB).isEmpty();

    }

    @Test
    void whenInterviewerSaved_findByCorrectIdShouldReturnCorrectInterviewer(){
        Interviewer interviewer1 = createAndSaveInterviewer(1L);

        Optional<Interviewer> optionalInterviewerFromDB = interviewerRepository.findById(interviewer1.getId());
        assertThat(optionalInterviewerFromDB).isPresent();

        Interviewer interviewerFromDB = optionalInterviewerFromDB.get();
        assertThat(interviewerFromDB.getEmail()).isEqualTo(interviewer1.getEmail());
        assertThat(interviewerFromDB.getName()).isEqualTo(interviewer1.getName());
        assertThat(interviewerFromDB.getId()).isEqualTo(interviewer1.getId());

    }

    @Test
    void whenInterviewerSaved_findByInvalidIdShouldReturnEmptyOptionalObject(){
        createAndSaveInterviewer(1L);
        
        Optional<Interviewer> optionalInterviewerFromDB = interviewerRepository.findById(-1L);
        assertThat(optionalInterviewerFromDB).isEmpty();

    }

    /* -- HELPER -- */
    private Interviewer createAndSaveInterviewer(long id) {
        Interviewer interviewer = new Interviewer();

        interviewer.setName("Carlos");
        interviewer.setEmail("carlos"+id+"@gmail.com");

        entityManager.persistAndFlush(interviewer);

        return interviewer;
    }
}
