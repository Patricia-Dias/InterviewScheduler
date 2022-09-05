package com.example.interviewscheduler.controllers;

import static org.mockito.ArgumentMatchers.any;
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
import com.example.interviewscheduler.dtos.InterviewSlotDTO;
import com.example.interviewscheduler.dtos.InterviewerDTO;
import com.example.interviewscheduler.exceptions.BadRequestException;
import com.example.interviewscheduler.exceptions.ConflictException;
import com.example.interviewscheduler.exceptions.PastDateException;
import com.example.interviewscheduler.exceptions.UnavailableSlotException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.services.InterviewSlotService;


import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;

@WebMvcTest(value = InterviewSlotController.class)
class InterviewSlotControllerTest {
    private final String API_INTERVIEWSLOT_ENDPOINT = "api/scheduler/interviewslot";
    private final int N_INTERVIEW_SLOT_ATTRIBUTES = 4;

    InterviewSlotDTO interviewSlotDTO;
    InterviewSlot interviewSlot;
    InterviewSlot unavailableInterviewSlot;
    List<InterviewSlot> allInterviewSlots;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InterviewSlotService interviewSlotService;

    @BeforeEach
    void setUp(){
        RestAssuredMockMvc.mockMvc(mvc);
        interviewSlotDTO = createInterviewSlotDTO();
        interviewSlot = createInterviewSlot();
        unavailableInterviewSlot = createInterviewSlot();
        unavailableInterviewSlot.setCandidate(createCandidate(1L));

        allInterviewSlots = List.of(interviewSlot, unavailableInterviewSlot);
    }

    /* ENDPOINT: api/scheduler/interviewSlot */
        // Post Slot
    @Test
    void testWhenCreateValidInterviewSlot_thenReturnCreatedInterviewSlot() throws BadRequestException, PastDateException, UserNotFoundException, ConflictException{
        when(interviewSlotService.create(interviewSlotDTO)).thenReturn(interviewSlot);

        given()
            .contentType( ContentType.JSON ).body( interviewSlotDTO ).post( API_INTERVIEWSLOT_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.CREATED ).and()
            .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            ;
        
        verify( interviewSlotService, times( 1 ) ).create( any() );
    }

    @Test
    void testWhenCreateInvalidInterviewSlot_thenReturnBadRequest() throws BadRequestException, PastDateException, UserNotFoundException, ConflictException{
        when(interviewSlotService.create(interviewSlotDTO)).thenThrow(PastDateException.class);

        given()
            .contentType( ContentType.JSON ).body( interviewSlotDTO ).post( API_INTERVIEWSLOT_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.BAD_REQUEST )
            ;

        verify( interviewSlotService, times( 1 ) ).create( any() );
    }

    @Test
    void testWhenCreateInterviewSlotInvalidInterviewer_thenReturnNotFound() throws BadRequestException, PastDateException, UserNotFoundException, ConflictException{
        when(interviewSlotService.create(interviewSlotDTO)).thenThrow(UserNotFoundException.class);

        given()
            .contentType( ContentType.JSON ).body( interviewSlotDTO ).post( API_INTERVIEWSLOT_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;
        
        verify( interviewSlotService, times( 1 ) ).create( any() );
    }

    @Test
    void testWhenCreateExistingInterviewSlot_thenReturnConflict() throws BadRequestException, PastDateException, UserNotFoundException, ConflictException{
        when(interviewSlotService.create(interviewSlotDTO)).thenThrow(ConflictException.class);

        given()
            .contentType( ContentType.JSON ).body( interviewSlotDTO ).post( API_INTERVIEWSLOT_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.CONFLICT )
            ;

        verify( interviewSlotService, times( 1 ) ).create( any() );
    }
        // Get Slot by ID
    @Test
    void testWhenGetInterviewValidId_thenReturnInterviewSlot(){
        when(interviewSlotService.get(interviewSlot.getId())).thenReturn(interviewSlot);

        given()
        .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT + "/{id}", interviewSlot.getId() )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            ;

        verify( interviewSlotService, times( 1 ) ).get( any() );
    }

    @Test
    void testWhenGetInterviewInvalidId_thenReturnNotFound(){
        when(interviewSlotService.get(interviewSlot.getId())).thenReturn(null);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT + "/{id}", interviewSlot.getId() )
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;

        verify( interviewSlotService, times( 1 ) ).get( any() );
    }

        // Get All Slots
    @Test
    void testWhenGetAllInterviewSlots_thenReturnList(){
        when(interviewSlotService.getAll()).thenReturn(allInterviewSlots);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT )
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(allInterviewSlots.size()))
            ;
        
        verify( interviewSlotService, times( 1 ) ).getAll();
    }

        // Put Candidate to Slot
    @Test
    void testWhenValidAssignSlotToCandidate_thenReturnUpdatedInterviewSlot() throws UserNotFoundException, BadRequestException, UnavailableSlotException, ConflictException{
        Candidate candidate = createCandidate(1L);
        interviewSlot.setCandidate(candidate);

        when(interviewSlotService.assignToCandidate(interviewSlot.getId(),candidate.getId())).thenReturn(interviewSlot);

        given()
            .contentType( ContentType.JSON ).put( API_INTERVIEWSLOT_ENDPOINT +"?slotId="+interviewSlot.getId()+"&candidateId="+candidate.getId())
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            ;

        verify( interviewSlotService, times( 1 ) ).assignToCandidate(any(), any());
    }

    @Test
    void testWhenValidAssignSlotToCandidate_thenReturnNotFound() throws UserNotFoundException, BadRequestException, UnavailableSlotException, ConflictException{
        Candidate candidate = createCandidate(1L);
        when(interviewSlotService.assignToCandidate(interviewSlot.getId(),candidate.getId())).thenThrow(UserNotFoundException.class);

        given()
            .contentType( ContentType.JSON ).put( API_INTERVIEWSLOT_ENDPOINT +"?slotId="+interviewSlot.getId()+"&candidateId="+candidate.getId())
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;

        verify( interviewSlotService, times( 1 ) ).assignToCandidate(any(), any());
    }

    @Test
    void testWhenValidAssignSlotToCandidate_thenReturnBadRequest() throws UserNotFoundException, BadRequestException, UnavailableSlotException, ConflictException{
        Candidate candidate = createCandidate(1L);
        when(interviewSlotService.assignToCandidate(interviewSlot.getId(),candidate.getId())).thenThrow(BadRequestException.class);

        given()
            .contentType( ContentType.JSON ).put( API_INTERVIEWSLOT_ENDPOINT +"?slotId="+interviewSlot.getId()+"&candidateId="+candidate.getId())
        .then().assertThat()
            .status( HttpStatus.BAD_REQUEST )
            ;
        
        verify( interviewSlotService, times( 1 ) ).assignToCandidate(any(), any());
    }

    @Test
    void testWhenValidAssignSlotToCandidate_thenReturnConflict()throws UserNotFoundException, BadRequestException, UnavailableSlotException, ConflictException{
        Candidate candidate = createCandidate(1L);
        when(interviewSlotService.assignToCandidate(interviewSlot.getId(),candidate.getId())).thenThrow(UnavailableSlotException.class);

        given()
            .contentType( ContentType.JSON ).put( API_INTERVIEWSLOT_ENDPOINT +"?slotId="+interviewSlot.getId()+"&candidateId="+candidate.getId())
        .then().assertThat()
            .status( HttpStatus.CONFLICT )
            ;
        
        verify( interviewSlotService, times( 1 ) ).assignToCandidate(any(), any());
    }

    /* ENDPOINT: api/scheduler/interviewSlot/available */
        // Get Available Slots
    @Test
    void testWhenGetAvailableInterviewSlots_thenReturnList(){
        List<InterviewSlot> availableSlots = List.of(interviewSlot);
        when(interviewSlotService.getAvailableSlots()).thenReturn(availableSlots);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT + "/available")
        .then().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(availableSlots.size()))
            ;
        
        verify( interviewSlotService, times( 1 ) ).getAvailableSlots();
    }
    /* ENDPOINT: api/scheduler/interviewSlot/availablebytime */
        // Get Available Slots by Time
    @Test
    void testWhenGetAvailableInterviewSlotsByValidTime_thenReturnInterviewSLot(){
        List<InterviewSlot> availableSlotByTime = List.of(interviewSlot);
        when(interviewSlotService.getAvailableSlotsByTime(interviewSlot.getTime())).thenReturn(availableSlotByTime);

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT + "/availablebytime?time="+interviewSlot.getTime())
        .then().log().body().assertThat()
            .status( HttpStatus.OK ).and()
            .body("size()", is(N_INTERVIEW_SLOT_ATTRIBUTES))
            ;
        
        verify( interviewSlotService, times( 1 ) ).getAvailableSlotsByTime(any());
    }

    @Test
    void testWhenGetAvailableInterviewSlotsByInvalidTime_thenReturnNotFound(){
        when(interviewSlotService.getAvailableSlotsByTime(interviewSlot.getTime())).thenReturn(List.of());

        given()
            .contentType( ContentType.JSON ).get( API_INTERVIEWSLOT_ENDPOINT + "/availablebytime?time="+interviewSlot.getTime())
        .then().assertThat()
            .status( HttpStatus.NOT_FOUND )
            ;

        verify( interviewSlotService, times( 1 ) ).getAvailableSlotsByTime(any());
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

    private InterviewSlotDTO createInterviewSlotDTO() {
        InterviewSlotDTO interviewSlotDTO = new InterviewSlotDTO();
        InterviewerDTO interviewerDTO = createInterviewerDTO(1L);
        interviewSlotDTO.setInterviewer(interviewerDTO);
        interviewSlotDTO.setTime(LocalDateTime.parse("2040-08-30T10:00:00"));

        return interviewSlotDTO;
    }

    private InterviewerDTO createInterviewerDTO(long id){
        InterviewerDTO interviewerDTO = new InterviewerDTO();
        interviewerDTO.setName("Carlos");
        interviewerDTO.setEmail("carlos"+id+"@gmail.com");

        return interviewerDTO;
    }

    private Candidate createCandidate(long id){
        Candidate candidate = new Candidate();
        candidate.setId(id);
        candidate.setEmail( "carl@gmail.com");
        candidate.setName("Carl");
        return candidate;
    }

}
