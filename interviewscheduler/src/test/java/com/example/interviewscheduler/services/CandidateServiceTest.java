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

import com.example.interviewscheduler.dtos.CandidateDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.repositories.CandidateRepository;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    
    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    private CandidateDTO candidateDTO;
    private Candidate candidate;

    @BeforeEach
    void setUp(){
        candidate = new Candidate(0L, "Ines", "ines@gmail.com", null);
        candidateDTO = new CandidateDTO("Ines", "ines@gmail.com");
    }

    /* Registering new Candidates */
    @Test
    void whenValidCandidateDTOObject_thenSaveCandidate_andReturnCandidate() throws DuplicatedUserException{
        when(candidateRepository.findByEmail(candidate.getEmail())).thenReturn(Optional.empty());

        Candidate candidateSaved = candidateService.register(candidateDTO);

        assertThat(candidateSaved.getEmail()).isEqualTo(candidate.getEmail());
        assertThat(candidateSaved.getName()).isEqualTo(candidate.getName());
        assertThat(candidateSaved.getId()).isEqualTo(candidate.getId());

        verifySaveCandidateIsCalledOnce();

    }

    @Test
    void whenEmailAlreadyInUse_thenThrowDuplicatedUserException(){
        when( candidateRepository.findByEmail( candidate.getEmail() ) ).thenReturn( Optional.of( candidate ) );

        assertThrows( DuplicatedUserException.class, () -> {
        candidateService.register( candidateDTO );
        } );

        verifySaveCandidateIsNeverCalled();
    }

    /* Finding Candidate by ID */
    @Test
    void whenValidId_thenReturnCandidate(){
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        Candidate candidateFromDB = candidateService.getCandidateById(candidate.getId());

        assertThat(candidateFromDB.getEmail()).isEqualTo(candidate.getEmail());
        assertThat(candidateFromDB.getName()).isEqualTo(candidate.getName());
        assertThat(candidateFromDB.getId()).isEqualTo(candidate.getId());

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenValidIdButNonExistentCandidate_thenReturnNullObject(){
        when( candidateRepository.findById( any() ) ).thenReturn( Optional.empty());

        Candidate candidateFromDB = candidateService.getCandidateById(10L);

        assertThat(candidateFromDB).isNull();
        
        verifyFindByIdIsCalledOnce();
    }

    /* Finding Candidate by Email */
    @Test
    void whenValidEmail_thenReturnCandidate(){
        when(candidateRepository.findByEmail(candidate.getEmail())).thenReturn(Optional.of(candidate));

        Candidate candidateFromDB = candidateService.getCandidateByEmail(candidate.getEmail());

        assertThat(candidateFromDB.getEmail()).isEqualTo(candidate.getEmail());
        assertThat(candidateFromDB.getName()).isEqualTo(candidate.getName());
        assertThat(candidateFromDB.getId()).isEqualTo(candidate.getId());

        verifyFindByEmailIsCalledOnce();
    }

    @Test
    void whenInvalidEmail_thenReturnNullObject(){

        when( candidateRepository.findByEmail( any() ) ).thenReturn( Optional.empty());

        Candidate candidateFromDB = candidateService.getCandidateByEmail("invalid@email.com");

        assertThat(candidateFromDB).isNull();

        verifyFindByEmailIsCalledOnce();

    }

    /* Getting Interview Slot by Candidate ID */
    @Test
    void whenGetAssignedInterviewSlot_thenReturnInterviewSlot() throws UserNotFoundException{
        candidate.setSlot(createInterviewSlot());
        candidate.getSlot().setCandidate(candidate);
        InterviewSlot candidateSlot = candidate.getSlot();

        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        InterviewSlot interviewSlotFomDB = candidateService.getInterviewSlotByCandidateId(candidate.getId());


        assertThat(interviewSlotFomDB.getInterviewer()).isEqualTo(candidateSlot.getInterviewer());
        assertThat(interviewSlotFomDB.getCandidate()).isNotNull();
        assertThat(interviewSlotFomDB.getCandidate()).isEqualTo(candidateSlot.getCandidate());
        assertThat(interviewSlotFomDB.getTime()).isEqualTo(candidateSlot.getTime());

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenGetUnassignedInterviewSlot_thenReturnNullObject() throws UserNotFoundException{
        when(candidateRepository.findById(candidate.getId())).thenReturn(Optional.of(candidate));

        InterviewSlot interviewSlot = candidateService.getInterviewSlotByCandidateId(candidate.getId());
        
        assertThat(interviewSlot).isNull();

        verifyFindByIdIsCalledOnce();
    }

    @Test
    void whenGetInterviewSlotOfUnexistingCandidate_thenThrowUserNotFoundException(){
        when( candidateRepository.findById( any() ) ).thenReturn( Optional.empty() );

        assertThrows( UserNotFoundException.class, () -> {
        candidateService.getInterviewSlotByCandidateId(10L);
        } );

        verifySaveCandidateIsNeverCalled();
    }

    /* Getting All Candidates */
    @Test
    void whenGetAllCandidates_thenReturnListOfCandidates() throws UserNotFoundException{
        Candidate candidate2 = new Candidate(1L, "candidate2", "candidate2@gmail.com", null);
        List<Candidate> listOfCandidates = List.of(candidate, candidate2);

        when(candidateRepository.findAll()).thenReturn(listOfCandidates);

        List<Candidate> listOfCandidatesFomDB = candidateService.getAllCandidates();

        assertThat(listOfCandidatesFomDB).hasSize(listOfCandidates.size()).isEqualTo(listOfCandidates);

        verifyFindAllIsCalledOnce();
    }

    /* --- HELPERS --- */
    void verifySaveCandidateIsCalledOnce(){
        verify( candidateRepository, VerificationModeFactory.times( 1 ) ).save( any() );
    }

    void verifySaveCandidateIsNeverCalled(){
        verify( candidateRepository, VerificationModeFactory.times( 0 ) ).save( any() );
    }

    void verifyFindByEmailIsCalledOnce(){
        verify( candidateRepository, VerificationModeFactory.times( 1 ) ).findByEmail( any() );
    }

    void verifyFindByIdIsCalledOnce(){
        verify( candidateRepository, VerificationModeFactory.times( 1 ) ).findById( any() );
    }

    private void verifyFindAllIsCalledOnce(){
        verify( candidateRepository, VerificationModeFactory.times( 1 ) ).findAll();
    }

    private InterviewSlot createInterviewSlot() {
        InterviewSlot interviewSlot = new InterviewSlot();
        Interviewer interviewer = createInterviewer(1L);

        interviewSlot.setInterviewer(interviewer);
        interviewSlot.setTime(LocalDateTime.parse("2042-08-30T10:00:00"));

        return interviewSlot;
    }

    private Interviewer createInterviewer(long id){
        Interviewer interviewer = new Interviewer();

        interviewer.setName("Carlos");
        interviewer.setEmail("carlos"+id+"@gmail.com");

        return interviewer;
    }

}
