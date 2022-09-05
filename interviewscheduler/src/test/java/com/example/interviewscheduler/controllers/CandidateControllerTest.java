package com.example.interviewscheduler.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.example.interviewscheduler.dtos.CandidateDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.services.CandidateService;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;

@WebMvcTest(value = CandidateController.class)
class CandidateControllerTest {
    private final String API_CANDIDATE_ENDPOINT = "api/scheduler/candidate";
    private final int N_CANDIDATE_ATTRIBUTES = 3;
    private final int N_INTERVIEW_SLOT_ATTRIBUTES = 4;

    CandidateDTO candidateDTO;
    Candidate candidate;
    Candidate candidate2;
    List<Candidate> allCandidates;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CandidateService candidateService;

    @BeforeEach
    void setUp(){
        RestAssuredMockMvc.mockMvc(mvc);
        candidateDTO = createCandidateDTO(1L);
        candidate = createCandidate(1L);
        candidate2 = createCandidate(2L);

        allCandidates = List.of(candidate,candidate2);
    }

    /* ENDPOINT: api/scheduler/candidate/register */
        // Post register
    @Test
    void testWhenRegisterValidCandidate_thenReturnCreatedCandidate() throws DuplicatedUserException {
        when(candidateService.register(candidateDTO)).thenReturn(candidate);

        given()
            .contentType( ContentType.JSON ).body( candidateDTO ).post( API_CANDIDATE_ENDPOINT + "/register" )
        .then().assertThat()
            .status( HttpStatus.CREATED ).and()
            .body("size()", is(N_CANDIDATE_ATTRIBUTES))
            ;
        
        verify( candidateService, times( 1 ) ).register( any() );
    }

    @Test
    void testWhenRegisterRegisteredCandidate_thenReturnConflict() throws DuplicatedUserException{
        when(candidateService.register(candidateDTO)).thenThrow(DuplicatedUserException.class);

        given()
            .contentType( ContentType.JSON ).body( candidateDTO ).post( API_CANDIDATE_ENDPOINT + "/register" )
        .then().assertThat()
            .status( HttpStatus.CONFLICT )
            ;

        verify( candidateService, times( 1 ) ).register( any() );
    }
        

    /* ENDPOINT: api/scheduler/candidate */
        // Get All Candidates
    @Test
    void testWhenGetAllCandidates_thenReturnList() {
        when(candidateService.getAllCandidates()).thenReturn(allCandidates);

        given()
            .contentType( ContentType.JSON ).get( API_CANDIDATE_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(allCandidates.size()))
            ;
        
        verify( candidateService, times( 1 ) ).getAllCandidates();
    }

        // Login Candidate
    @Test
    void testWhenloginValidCandidate_thenReturnCandidate() throws DuplicatedUserException{
        when(candidateService.getCandidateByEmail(candidate.getEmail())).thenReturn(candidate);

        given()
            .contentType( ContentType.JSON ).post( API_CANDIDATE_ENDPOINT +"/{email}", candidate.getEmail())
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_CANDIDATE_ATTRIBUTES))
            ;
        
        verify( candidateService, times( 1 ) ).getCandidateByEmail( any() );
    }

    @Test
    void testWhenCreateExistingCandidate_thenReturnNotFound() throws DuplicatedUserException{
        when(candidateService.getCandidateByEmail(candidate.getEmail())).thenReturn(null);

        given()
            .contentType( ContentType.JSON ).post( API_CANDIDATE_ENDPOINT +"/{email}", candidate.getEmail())
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;

        verify( candidateService, times( 1 ) ).getCandidateByEmail( any() );
    }
    
    /* ENDPOINT: api/scheduler/candidate/interviewslot/{id} */
        // Get Slot by Candidate ID
    @Test
    void testWhenGetInterviewSlotsByValidCandidateID_thenReturnSet() throws UserNotFoundException{
        InterviewSlot interviewSlot = createInterviewSlot();
        // Set<InterviewSlot> slots = Set.of(interviewSlot);

        when(candidateService.getInterviewSlotByCandidateId(candidate.getId())).thenReturn(interviewSlot);

        given()
            .contentType( ContentType.JSON ).get( API_CANDIDATE_ENDPOINT + "/interviewslot/{id}", candidate.getId() )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            ;

        verify( candidateService, times( 1 ) ).getInterviewSlotByCandidateId(anyLong());
    }

    @Test
    void testWhenGetInterviewSlotsByInvalidCandidateID_thenReturnNotFound() throws UserNotFoundException{
        when(candidateService.getInterviewSlotByCandidateId(candidate.getId())).thenThrow(UserNotFoundException.class);

        given()
            .contentType( ContentType.JSON ).get( API_CANDIDATE_ENDPOINT + "/interviewslot/{id}", candidate.getId() )
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
        ;

        verify( candidateService, times( 1 ) ).getInterviewSlotByCandidateId(anyLong());
    }

    /* --- HELPERS --- */

    private InterviewSlot createInterviewSlot() {
        InterviewSlot interviewSlot = new InterviewSlot();
        Interviewer interviewer = createInterviewer(1L);
        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(LocalDateTime.parse("2040-08-30T10:00:00"));

        return interviewSlot;
    }

    private Candidate createCandidate(long id){
        Candidate candidate = new Candidate();
        candidate.setName("Carlos");
        candidate.setEmail("carlos"+id+"@gmail.com");

        return candidate;
    }

    private CandidateDTO createCandidateDTO(long id){
        CandidateDTO candidateDTO = new CandidateDTO();
        candidateDTO.setName("Carlos");
        candidateDTO.setEmail("carlos"+id+"@gmail.com");

        return candidateDTO;
    }

    private Interviewer createInterviewer(long id){
        Interviewer interviewer = new Interviewer();
        interviewer.setName("Carlos");
        interviewer.setEmail("carlos"+id+"@gmail.com");

        return interviewer;
    }
}
