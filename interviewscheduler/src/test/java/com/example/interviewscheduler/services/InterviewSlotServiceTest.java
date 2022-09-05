package com.example.interviewscheduler.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.example.interviewscheduler.repositories.CandidateRepository;
import com.example.interviewscheduler.repositories.InterviewSlotRepository;
import com.example.interviewscheduler.repositories.InterviewerRepository;

@ExtendWith(MockitoExtension.class)
class InterviewSlotServiceTest {
    @Mock(lenient = true)
    private InterviewSlotRepository interviewSlotRepository;

    @Mock(lenient = true)
    private CandidateRepository candidateRepository;

    @Mock
    private InterviewerRepository interviewerRepository;

    @InjectMocks
    private InterviewSlotService interviewSlotService;

    private InterviewSlotDTO interviewSlotDTO;
    private InterviewSlot interviewSlot;

    @BeforeEach
    void setUp(){
        interviewSlot = createInterviewSlot(LocalDateTime.parse("2040-08-30T10:00:00"));
        interviewSlotDTO = createInterviewSlotDTO();
    }


    /* Creating Interview Slot */
    @Test
    void whenValidInterviewSlotDTOObject_thenSaveInterviewSlot_andReturnInterviewSlot() throws Exception{
        Interviewer interviewer = createInterviewer(1L);
        when(interviewerRepository.findByEmail(interviewer.getEmail())).thenReturn(Optional.of(interviewer));
        
        interviewSlotService.create(interviewSlotDTO);

        assertThat(interviewSlot.getInterviewer()).isEqualTo(interviewSlot.getInterviewer());
        assertThat(interviewSlot.getTime()).isEqualTo(interviewSlot.getTime());
        assertThat(interviewSlot.getId()).isEqualTo(interviewSlot.getId());

        verifySaveInterviewSlotIsCalledOnce();
    }

    @Test
    void whenInterviewSlotExists_thenThrowConflictException(){
        Interviewer interviewer = createInterviewer(1L);

        when(interviewerRepository.findByEmail(interviewer.getEmail())).thenReturn(Optional.of(interviewer));
        when(interviewSlotRepository.findByTimeAndInterviewer(any(), any())).thenReturn(Optional.of(interviewSlot));

        assertThrows( ConflictException.class, () -> {
        interviewSlotService.create( interviewSlotDTO );
        } );

        verifySaveInterviewSlotIsNeverCalled();
    }

    @Test
    void whenInterviewSlotDTOObjectWithNullInterviewer_thenThrowBadRequestException(){
        interviewSlotDTO.setInterviewer(null);

        assertThrows( BadRequestException.class, () -> {
        interviewSlotService.create( interviewSlotDTO );
        } );

        verifySaveInterviewSlotIsNeverCalled();
    }

    @Test
    void whenInterviewSlotDTOObjectWithNullDate_thenThrowBadRequestException(){
        interviewSlotDTO.setTime(null);

        assertThrows( BadRequestException.class, () -> {
        interviewSlotService.create( interviewSlotDTO );
        } );

        verifySaveInterviewSlotIsNeverCalled();
    }

    @Test
    void whenInterviewSlotDTOObjectWithPastDate_thenThrowPastDateException(){
        interviewSlotDTO.setTime(LocalDateTime.parse("2020-08-30T10:00:00"));

        assertThrows( PastDateException.class, () -> {
        interviewSlotService.create( interviewSlotDTO );
        } );

        verifySaveInterviewSlotIsNeverCalled();
        
    }

    /* Getting All Interview Slots */
    @Test
    void whenGetAll_thenReturnList(){
        List<InterviewSlot> listOfSlots = List.of(
            createInterviewSlot(LocalDateTime.parse("2040-08-30T10:00:00")),
            createInterviewSlot(LocalDateTime.parse("2042-08-31T10:00:00")),
            createInterviewSlot(LocalDateTime.parse("2044-09-01T10:00:00"))
            );
        when(interviewSlotRepository.findAll()).thenReturn(listOfSlots);

        List<InterviewSlot> listOfInterviewSlotsFromDB = interviewSlotService.getAll();

        assertThat(listOfInterviewSlotsFromDB).hasSize(listOfSlots.size()).isEqualTo(listOfSlots);

        verifyFindAllIsCalledOnce();
    }


    /* Assigning Interview Slot to a Candidate */
    @Test
    void whenAssignInterviewSlotWithExistentCandidateId_thenReturnUpdatedInterviewSlot() throws Exception{
        Candidate candidate = createCandidate(0L);
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(interviewSlotRepository.findById(interviewSlot.getId())).thenReturn(Optional.of(interviewSlot));

        InterviewSlot interviewSlotFromDB = interviewSlotService.assignToCandidate(interviewSlot.getId(), candidate.getId());

        assertThat(interviewSlotFromDB.getId()).isEqualTo(interviewSlot.getId());
        assertThat(interviewSlotFromDB.getTime()).isEqualTo(interviewSlot.getTime());
        assertThat(interviewSlotFromDB.getInterviewer()).isEqualTo(interviewSlot.getInterviewer());
        assertThat(interviewSlotFromDB.getCandidate().getName()).isEqualTo(candidate.getName());
        assertThat(interviewSlotFromDB.getCandidate().getEmail()).isEqualTo(candidate.getEmail());

        verifySaveInterviewSlotIsCalledOnce();
    }

    @Test
    void whenAssignInterviewSlotWithNonExistingSlotId_thenThrowBadRequestException(){
        Candidate candidate = createCandidate(1L);
        when(interviewSlotRepository.findById(any())).thenReturn(Optional.empty());
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        assertThrows( BadRequestException.class, () -> {
            interviewSlotService.assignToCandidate(-1L, any());
            } );
    
            verifySaveInterviewSlotIsNeverCalled();
    }

    @Test
    void whenAssignOccupiedInterviewSlot_thenThrowUnavailableSlotException() {
        Candidate candidate = createCandidate(1L);
        interviewSlot.setCandidate(candidate);
        when(interviewSlotRepository.findById(interviewSlot.getId())).thenReturn(Optional.of(interviewSlot));
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        assertThrows( UnavailableSlotException.class, () -> {
            interviewSlotService.assignToCandidate(interviewSlot.getId(), candidate.getId());
            } );

        verifySaveInterviewSlotIsNeverCalled();

    }

    @Test
    void whenAssignInterviewSlotWithNonExistingCandidateId_thenThrowUserNotFoundException(){
        when(interviewSlotRepository.findById(interviewSlot.getId())).thenReturn(Optional.of(interviewSlot));
        when(candidateRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows( UserNotFoundException.class, () -> {
            interviewSlotService.assignToCandidate(interviewSlot.getId(), -1L);
            } );
    
        verifySaveInterviewSlotIsNeverCalled();
    }

    void whenAssignToOccupiedCandidate_thenThrowConflictException() {
        Candidate candidate = createCandidate(1L);
        interviewSlot.setId(5L);
        candidate.setSlot(interviewSlot);
        interviewSlot.setId(1L);

        when(interviewSlotRepository.findById(interviewSlot.getId())).thenReturn(Optional.of(interviewSlot));
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        assertThrows( ConflictException.class, () -> {
            interviewSlotService.assignToCandidate(interviewSlot.getId(), candidate.getId());
            } );

        verifySaveInterviewSlotIsNeverCalled();

    }

    /* Getting Interview Slot by ID */
    @Test
    void whenValidId_thenReturnInterviewSlot(){
        when(interviewSlotRepository.findById(interviewSlot.getId())).thenReturn(Optional.of(interviewSlot));

        InterviewSlot interviewSlotFromDB = interviewSlotService.get(interviewSlot.getId());

        assertThat(interviewSlotFromDB.getTime()).isEqualTo(interviewSlot.getTime());
        assertThat(interviewSlotFromDB.getInterviewer()).isEqualTo(interviewSlot.getInterviewer());
        assertThat(interviewSlotFromDB.getCandidate()).isEqualTo(interviewSlot.getCandidate());

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenValidIdButNonExistentInterviewSlot_thenReturnNullObject(){
        when(interviewSlotRepository.findById(any())).thenReturn(Optional.empty());

        InterviewSlot interviewSlotFromDB = interviewSlotService.get(5L);

        assertThat(interviewSlotFromDB).isNull();
        
        verifyFindByIdIsCalledOnce();
    }

    /* Getting Available Interview Slots */
    @Test
    void whenGetAvailableSlots_thenReturnLisOfInterviewSlot(){
        List<InterviewSlot> listOfSlots = List.of(
            createInterviewSlot(LocalDateTime.parse("2042-08-30T10:00:00")),
            createInterviewSlot(LocalDateTime.parse("2042-09-01T10:00:00"))
            );

        when(interviewSlotRepository.findAvailableSlots()).thenReturn(listOfSlots);

        List<InterviewSlot> listOfInterviewSlotsFromDB = interviewSlotService.getAvailableSlots();

        assertThat(listOfInterviewSlotsFromDB).hasSize(listOfSlots.size()).isEqualTo(listOfSlots);

        verifyFindAvailableSlotsIsCalledOnce();
    }

    /* Getting Available Interview Slots by Time*/
    @Test
    void whenGetAvailableSlotsByTime_thenReturnLisOfInterviewSlot(){
        InterviewSlot interviewSlot = createInterviewSlot(LocalDateTime.parse("2042-08-30T10:00:00"));
        List<InterviewSlot> listOfSlots = List.of(interviewSlot);
            
        when(interviewSlotRepository.findAvailableSlotsByTime(any())).thenReturn(listOfSlots);

        List<InterviewSlot> listOfInterviewSlotsFromDB = interviewSlotService.getAvailableSlotsByTime(interviewSlot.getTime());

        assertThat(listOfInterviewSlotsFromDB).hasSize(listOfSlots.size()).isEqualTo(listOfSlots);

        verifyFindAvailableSlotsByAvailableIsCalledOnce();
    }

    /* -- HELPERS -- */
    void verifySaveInterviewSlotIsCalledOnce(){
        verify( interviewSlotRepository, VerificationModeFactory.times( 1 ) ).save( any() );
    }

    private void verifySaveInterviewSlotIsNeverCalled() {
        verify( interviewSlotRepository, VerificationModeFactory.times( 0 ) ).save( any() );
    }

    private void verifyFindAllIsCalledOnce() {
        verify( interviewSlotRepository, VerificationModeFactory.times( 1 ) ).findAll();
    }

    private void verifyFindByIdIsCalledOnce() {
        verify( interviewSlotRepository, VerificationModeFactory.times( 1 ) ).findById(any());
    }

    private void verifyFindAvailableSlotsIsCalledOnce(){
        verify(interviewSlotRepository, VerificationModeFactory.times(1)).findAvailableSlots();
    }

    private void verifyFindAvailableSlotsByAvailableIsCalledOnce(){
        verify(interviewSlotRepository, VerificationModeFactory.times(1)).findAvailableSlotsByTime(any());
    }

    private InterviewSlot createInterviewSlot(LocalDateTime time) {
        InterviewSlot interviewSlot = new InterviewSlot();
        Interviewer interviewer = createInterviewer(1L);

        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(time);

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
        interviewSlotDTO.setTime(LocalDateTime.parse("2042-08-30T10:00:00"));

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
