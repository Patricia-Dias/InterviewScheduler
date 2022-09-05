package com.example.interviewscheduler.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.interviewscheduler.dtos.InterviewerDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.repositories.InterviewerRepository;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

    @Mock
    private InterviewerRepository interviewerRepository;

    @InjectMocks
    private InterviewerService interviewerService;

    private InterviewerDTO interviewerDTO;
    private Interviewer interviewer;

    @BeforeEach
    void setUp(){
        interviewer = new Interviewer(0L, "Ines", "ines@gmail.com", null);
        interviewerDTO = new InterviewerDTO("Ines", "ines@gmail.com");
    }

    /* Registering new Interviewers */
    @Test
    void whenValidInterviewerDTOObject_thenSaveInterviewer_andReturnInterviewer() throws DuplicatedUserException{
        when(interviewerRepository.findByEmail(interviewer.getEmail())).thenReturn(Optional.empty());

        Interviewer interviewerSaved = interviewerService.register(interviewerDTO);

        assertThat(interviewerSaved.getEmail()).isEqualTo(interviewer.getEmail());
        assertThat(interviewerSaved.getName()).isEqualTo(interviewer.getName());
        assertThat(interviewerSaved.getId()).isEqualTo(interviewer.getId());
        // when an interviewer is registered, it starts with no slots
        assertThat(interviewerSaved.getSlots()).isNull();

        verifySaveInterviewerIsCalledOnce();

    }

    @Test
    void whenEmailAlreadyInUse_thenThrowDuplicatedUserException(){
        when( interviewerRepository.findByEmail( interviewer.getEmail() ) ).thenReturn( Optional.of( interviewer ) );

        assertThrows( DuplicatedUserException.class, () -> {
        interviewerService.register( interviewerDTO );
        } );

        verifySaveInterviewerIsNeverCalled();
    }

    /* Finding Interviewer by ID */
    @Test
    void whenValidId_thenReturnInterviewer(){
        when(interviewerRepository.findById(interviewer.getId())).thenReturn(Optional.of(interviewer));

        Interviewer interviewerFromDB = interviewerService.getInterviewerById(interviewer.getId());

        assertThat(interviewerFromDB.getEmail()).isEqualTo(interviewer.getEmail());
        assertThat(interviewerFromDB.getName()).isEqualTo(interviewer.getName());
        assertThat(interviewerFromDB.getId()).isEqualTo(interviewer.getId());

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenValidIdButNonExistentInterviewer_thenReturnNullObject(){
        when( interviewerRepository.findById( any() ) ).thenReturn( Optional.empty());

        Interviewer interviewerFromDB = interviewerService.getInterviewerById(10L);

        assertThat(interviewerFromDB).isNull();
        
        verifyFindByIdIsCalledOnce();
    }

    /* Finding Interviewer by Email */
    @Test
    void whenValidEmail_thenReturnInterviewer(){
        when(interviewerRepository.findByEmail(interviewer.getEmail())).thenReturn(Optional.of(interviewer));

        Interviewer interviewerFromDB = interviewerService.getInterviewerByEmail(interviewer.getEmail());

        assertThat(interviewerFromDB.getEmail()).isEqualTo(interviewer.getEmail());
        assertThat(interviewerFromDB.getName()).isEqualTo(interviewer.getName());
        assertThat(interviewerFromDB.getId()).isEqualTo(interviewer.getId());

        verifyFindByEmailIsCalledOnce();
    }

    @Test
    void whenInvalidEmail_thenReturnNullObject(){

        when( interviewerRepository.findByEmail( any() ) ).thenReturn( Optional.empty());

        Interviewer interviewerFromDB = interviewerService.getInterviewerByEmail("invalid@email.com");

        assertThat(interviewerFromDB).isNull();

        verifyFindByEmailIsCalledOnce();

    }

    /* Getting Interviewer's Interview Slots */
    @Test
    void whenGetInterviewSlots_thenReturnSetOfInterviewSlots() throws UserNotFoundException{
        Set<InterviewSlot> setOfSlots = Set.of(
            createInterviewSlot(LocalDateTime.parse("2022-08-30T10:00:00")),
            createInterviewSlot(LocalDateTime.parse("2022-08-30T11:00:00")),
            createInterviewSlot(LocalDateTime.parse("2022-08-30T12:00:00"))
            );
        interviewer.setSlots(setOfSlots);

        when(interviewerRepository.findById(interviewer.getId())).thenReturn(Optional.of(interviewer));

        Set<InterviewSlot> interviewSlotsFromDB = interviewerService.getAllInterviewSlotsByInterviewer(interviewer.getId());
        assertThat(interviewSlotsFromDB).hasSize(setOfSlots.size()).isEqualTo(setOfSlots);

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenGetInterviewSlotsAndInterviewerHasNoSlots_thenReturnEmptySet() throws UserNotFoundException{
        when(interviewerRepository.findById(interviewer.getId())).thenReturn(Optional.of(interviewer));

        Set<InterviewSlot> interviewSlotsFromDB = interviewerService.getAllInterviewSlotsByInterviewer(interviewer.getId());
        assertThat(interviewSlotsFromDB).isEmpty();

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenGetInterviewSlotsOfUnexistingInterviewer_thenThrowUserNotFoundException(){
        when( interviewerRepository.findById( any() ) ).thenReturn( Optional.empty() );

        assertThrows( UserNotFoundException.class, () -> {
        interviewerService.getAllInterviewSlotsByInterviewer(10L);
        } );

        verifyFindAllIsNeverCalled();
    }

    /* Getting All Interviewers */
    @Test
    void whenGetAllInterviewers_thenReturnListOfInterviewers() throws UserNotFoundException{
        Interviewer interviewer2 = new Interviewer(1L, "interviewer2", "interviewer2@gmail.com", null);
        List<Interviewer> listOfInterviewers = List.of(interviewer, interviewer2);

        when(interviewerRepository.findAll()).thenReturn(listOfInterviewers);

        List<Interviewer> listOfInterviewersFomDB = interviewerService.getAllInterviewers();

        assertThat(listOfInterviewersFomDB).hasSize(listOfInterviewers.size()).isEqualTo(listOfInterviewers);

        verifyFindAllIsCalledOnce();
    }

    /* --- HELPERS --- */
    void verifySaveInterviewerIsCalledOnce(){
        verify( interviewerRepository, VerificationModeFactory.times( 1 ) ).save( any() );
    }

    void verifySaveInterviewerIsNeverCalled(){
        verify( interviewerRepository, VerificationModeFactory.times( 0 ) ).save( any() );
    }

    void verifyFindByEmailIsCalledOnce(){
        verify( interviewerRepository, VerificationModeFactory.times( 1 ) ).findByEmail( any() );
    }

    void verifyFindByIdIsCalledOnce(){
        verify( interviewerRepository, VerificationModeFactory.times( 1 ) ).findById( any() );
    }

    void verifyFindAllIsNeverCalled(){
        verify( interviewerRepository, VerificationModeFactory.times( 0 ) ).findAll();
    }

    private void verifyFindAllIsCalledOnce(){
        verify( interviewerRepository, VerificationModeFactory.times( 1 ) ).findAll();
    }

    private InterviewSlot createInterviewSlot(LocalDateTime time) {
        InterviewSlot interviewSlot = new InterviewSlot();

        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(time);

        return interviewSlot;
    }
}
