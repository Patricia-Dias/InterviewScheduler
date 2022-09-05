package com.example.interviewscheduler.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import com.example.interviewscheduler.dtos.InterviewerDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.services.InterviewerService;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;

@WebMvcTest(value = InterviewerController.class)
class InterviewerControllerTest {
    private final String API_INTERVIEWER_ENDPOINT = "api/scheduler/interviewer";
    private final int N_INTERVIEWER_ATTRIBUTES = 3;

    InterviewerDTO interviewerDTO;
    Interviewer interviewer;
    Interviewer interviewer2;
    List<Interviewer> allInterviewers;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InterviewerService interviewerService;

    @BeforeEach
    void setUp(){
        RestAssuredMockMvc.mockMvc(mvc);
        interviewerDTO = createInterviewerDTO(1L);
        interviewer = createInterviewer(1L);
        interviewer2 = createInterviewer(2L);

        allInterviewers = List.of(interviewer,interviewer2);
    }

    /* ENDPOINT: api/scheduler/interviewer/register */
        // Post register
    @Test
    void testWhenRegisterValidInterviewer_thenReturnCreatedInterviewer() throws DuplicatedUserException {
        when(interviewerService.register(interviewerDTO)).thenReturn(interviewer);

        given()
            .contentType( ContentType.JSON ).body( interviewerDTO ).post( API_INTERVIEWER_ENDPOINT + "/register" )
        .then().assertThat()
            .status( HttpStatus.CREATED ).and()
            .body("size()", is(N_INTERVIEWER_ATTRIBUTES))
            ;
        
        verify( interviewerService, times( 1 ) ).register( any() );
    }

    @Test
    void testWhenRegisterRegisteredInterviewer_thenReturnConflict() throws DuplicatedUserException{
        when(interviewerService.register(interviewerDTO)).thenThrow(DuplicatedUserException.class);

        given()
            .contentType( ContentType.JSON ).body( interviewerDTO ).post( API_INTERVIEWER_ENDPOINT + "/register" )
        .then().assertThat()
            .status( HttpStatus.CONFLICT )
            ;

        verify( interviewerService, times( 1 ) ).register( any() );
    }
        

    /* ENDPOINT: api/scheduler/interviewer */
        // Get All Interviewers
    @Test
    void testWhenGetAllInterviewers_thenReturnList() {
        when(interviewerService.getAllInterviewers()).thenReturn(allInterviewers);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWER_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(allInterviewers.size()))
            ;
        
        verify( interviewerService, times( 1 ) ).getAllInterviewers();
    }

        // Login Interviewer
    @Test
    void testWhenloginValidInterviewer_thenReturnInterviewer() throws DuplicatedUserException{
        when(interviewerService.getInterviewerByEmail(interviewer.getEmail())).thenReturn(interviewer);

        given()
            .contentType( ContentType.JSON ).post( API_INTERVIEWER_ENDPOINT +"/{email}", interviewer.getEmail())
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_INTERVIEWER_ATTRIBUTES))
            ;
        
        verify( interviewerService, times( 1 ) ).getInterviewerByEmail( any() );
    }

    @Test
    void testWhenCreateExistingInterviewer_thenReturnNotFound() throws DuplicatedUserException{
        when(interviewerService.getInterviewerByEmail(interviewer.getEmail())).thenReturn(null);

        given()
            .contentType( ContentType.JSON ).post( API_INTERVIEWER_ENDPOINT +"/{email}", interviewer.getEmail())
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;

        verify( interviewerService, times( 1 ) ).getInterviewerByEmail( any() );
    }
    
    /* ENDPOINT: api/scheduler/interviewer/interviewslot/{id} */
        // Get Slot by Interviewer ID
    @Test
    void testWhenGetInterviewSlotsByValidInterviewerID_thenReturnSet() throws UserNotFoundException{
        InterviewSlot interviewSlot = createInterviewSlot();
        interviewSlot.setInterviewer(interviewer);
        Set<InterviewSlot> slots = Set.of(interviewSlot);

        when(interviewerService.getAllInterviewSlotsByInterviewer(interviewer.getId())).thenReturn(slots);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWER_ENDPOINT + "/interviewslot/{id}", interviewer.getId() )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(slots.size()))
            ;

        verify( interviewerService, times( 1 ) ).getAllInterviewSlotsByInterviewer(anyLong());
    }

    @Test
    void testWhenGetInterviewSlotsByInvalidInterviewerID_thenReturnNotFound() throws UserNotFoundException{
        when(interviewerService.getAllInterviewSlotsByInterviewer(interviewer.getId())).thenThrow(UserNotFoundException.class);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWER_ENDPOINT + "/interviewslot/{id}", interviewer.getId() )
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
        ;

        verify( interviewerService, times( 1 ) ).getAllInterviewSlotsByInterviewer(anyLong());
    }

    /* --- HELPERS --- */

    private InterviewSlot createInterviewSlot() {
        InterviewSlot interviewSlot = new InterviewSlot();
        Interviewer interviewer = createInterviewer(1L);
        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(LocalDateTime.parse("2040-08-30T10:00:00"));

        return interviewSlot;
    }

    private Interviewer createInterviewer(long id){
        Interviewer interviewer = new Interviewer();
        interviewer.setName("Carlos");
        interviewer.setEmail("carlos"+id+"@gmail.com");

        return interviewer;
    }

    private InterviewerDTO createInterviewerDTO(long id){
        InterviewerDTO interviewerDTO = new InterviewerDTO();
        interviewerDTO.setName("Carlos");
        interviewerDTO.setEmail("carlos"+id+"@gmail.com");

        return interviewerDTO;
    }
}
