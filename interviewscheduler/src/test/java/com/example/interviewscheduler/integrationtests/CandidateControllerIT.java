package com.example.interviewscheduler.integrationtests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.interviewscheduler.dtos.CandidateDTO;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.repositories.CandidateRepository;
import com.example.interviewscheduler.repositories.InterviewSlotRepository;

import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CandidateControllerIT {
    private final String API_CANDIDATE_ENDPOINT = "api/scheduler/candidate";

    @LocalServerPort
    int randomServerPort;
    
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private InterviewSlotRepository interviewSlotRepository;

    Candidate candidate;
    CandidateDTO candidateDTO;

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

    @BeforeEach
    void setUp() {
        candidate = new Candidate();

        candidate.setName("Carlos");
        candidate.setEmail("carlos@gmail.com");

        candidate = candidateRepository.saveAndFlush(candidate);;
        candidateDTO  = new CandidateDTO("Carlos", "carlos@gmail.com");
    }

    @AfterEach
    void resetDb() {
        candidateRepository.deleteAll();
        candidateRepository.flush();
    }

    /* ENDPOINT: api/scheduler/candidate/register */
        // Post register
        @Test
        void testWhenRegisterValidCandidate_thenReturn201() {
            CandidateDTO newCandidate  = new CandidateDTO("Carlos", "carlos@gmail.com");
            given()
                .contentType( "application/json" )
                .body( newCandidate )
            .when()
                .post( API_CANDIDATE_ENDPOINT + "/register" )
            .then()
                .body("name", equalTo(newCandidate.getName()))
                .body("email", equalTo(newCandidate.getEmail()))
                .statusCode( 201 )
                ;
            
        }
    
        @Test
        void testWhenRegisterRegisteredCandidate_thenReturnConflict() {
    
            given()
                .contentType( "application/json" )
                .body( candidateDTO )
            .when()
                .post( API_CANDIDATE_ENDPOINT + "/register" )
            .then()
                .statusCode( 409 )
                ;
        
        }
            
    
        /* ENDPOINT: api/scheduler/candidate */
            // Get All Candidates
        @Test
        void testWhenGetAllCandidates_thenReturn200() {

            given()
                .contentType( "application/json" )
            .when()
                .get( API_CANDIDATE_ENDPOINT )
            .then()
                .body("size()", equalTo(1))
                .statusCode( 200 )
                ;
            
        }
    
            // Login Candidate
        @Test
        void testWhenloginValidCandidate_thenReturnCandidate() {
    
            given()
                .contentType( "application/json" )
            .when()
                .post( API_CANDIDATE_ENDPOINT +"/{email}", candidate.getEmail())
            .then()
                .body("name", equalTo(candidate.getName()))
                .body("email", equalTo(candidate.getEmail()))
                .statusCode( 200 )
                ;
            
        }
    
        @Test
        void testWhenLoginNonExistingCandidate_thenReturnNotFound() {

            given()
                .contentType( "application/json" )
            .when()
                .post( API_CANDIDATE_ENDPOINT + "/new@email.com" )
            .then()
                .statusCode( 404 )
                ;
    
        }
        
        /* ENDPOINT: api/scheduler/candidate/interviewslot/{id} */
            // Get Slot by Candidate ID
        @Test
        void testWhenGetInterviewSlotsByValidCandidateID_thenReturnSet() {
            InterviewSlot interviewSlot = createInterviewSlot();
            interviewSlot.setCandidate(candidate);
            interviewSlotRepository.saveAndFlush(interviewSlot);
            // Set<InterviewSlot> slots = Set.of(interviewSlot);
    
    
            // given()
            //     .contentType( ContentType.JSON ).get( API_CANDIDATE_ENDPOINT + "/interviewslot/{id}", candidate.getId() )
            // .then().assertThat()
            //     .status( HttpStatus.OK ).and()
            //     .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            //     ;

            given()
                .contentType( "application/json" )
            .when()
                .get( API_CANDIDATE_ENDPOINT +"/interviewslot/{id}", candidate.getId())
            .then()
                .body("name", equalTo(candidate.getName()))
                .body("email", equalTo(candidate.getEmail()))
                .statusCode( 200 )
                ;
    
        }
    
        @Test
        void testWhenGetInterviewSlotsByInvalidCandidateID_thenReturnNotFound() {
    
            given()
                .contentType( "application/json" )
            .when()
                .get( API_CANDIDATE_ENDPOINT +"/interviewslot/{id}", 1)
            .then()
                .statusCode( 404 )
                ;
        }

        /* --- HELPERS --- */
        private InterviewSlot createInterviewSlot() {
            InterviewSlot interviewSlot = new InterviewSlot();
            Interviewer interviewer = createInterviewer();
            interviewSlot.setInterviewer(interviewer);
            interviewSlot.setTime(LocalDateTime.parse("2040-08-30T10:00:00"));
    
            return interviewSlot;
        }
        private Interviewer createInterviewer(){
            Interviewer interviewer = new Interviewer();
            interviewer.setName("Carlos");
            interviewer.setEmail("carlos@gmail.com");
    
            return interviewer;
        }
    
}
