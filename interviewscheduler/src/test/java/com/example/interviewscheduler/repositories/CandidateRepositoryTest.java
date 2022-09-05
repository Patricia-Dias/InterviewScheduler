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

import com.example.interviewscheduler.models.Candidate;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CandidateRepositoryTest {
    
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
    private CandidateRepository candidateRepository;

    @Autowired
    private TestEntityManager entityManager;

    @AfterEach
    void resetDB(){
        candidateRepository.deleteAll();
        entityManager.clear();
    }

    @Test
    void whenCandidateSaved_findByCorrectEmailShouldReturnCorrectCandidate(){
        Candidate candidate1 = createAndSaveCandidate(1L);
        
        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findByEmail(candidate1.getEmail());
        assertThat(optionalCandidateFromDB).isPresent();

        Candidate candidateFromDB = optionalCandidateFromDB.get();
        assertThat(candidateFromDB.getEmail()).isEqualTo(candidate1.getEmail());
        assertThat(candidateFromDB.getName()).isEqualTo(candidate1.getName());
        assertThat(candidateFromDB.getId()).isEqualTo(candidate1.getId());
    }

    @Test
    void whenCandidateSaved_findByIncorrectEmailShouldReturnEmptyOptionalObject(){
        createAndSaveCandidate(1L);
        createAndSaveCandidate(2L);
        
        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findByEmail("invalid@email.com");
        assertThat(optionalCandidateFromDB).isEmpty();

    }

    @Test
    void whenCandidateSaved_findByCorrectIdShouldReturnCorrectCandidate(){
        Candidate candidate1 = createAndSaveCandidate(1L);

        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findById(candidate1.getId());
        assertThat(optionalCandidateFromDB).isPresent();

        Candidate candidateFromDB = optionalCandidateFromDB.get();
        assertThat(candidateFromDB.getEmail()).isEqualTo(candidate1.getEmail());
        assertThat(candidateFromDB.getName()).isEqualTo(candidate1.getName());
        assertThat(candidateFromDB.getId()).isEqualTo(candidate1.getId());

    }

    @Test
    void whenCandidateSaved_findByInvalidIdShouldReturnEmptyOptionalObject(){
        createAndSaveCandidate(1L);
        
        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findById(-1L);
        assertThat(optionalCandidateFromDB).isEmpty();

    }

    /* -- HELPER -- */
    private Candidate createAndSaveCandidate(long id) {
        Candidate candidate = new Candidate();

        candidate.setName("Carlos");
        candidate.setEmail("carlos"+id+"@gmail.com");

        entityManager.persistAndFlush(candidate);

        return candidate;
    }
    
}
