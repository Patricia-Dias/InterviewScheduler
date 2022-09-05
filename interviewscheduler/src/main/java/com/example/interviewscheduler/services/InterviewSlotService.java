package com.example.interviewscheduler.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class InterviewSlotService {
    @Autowired
    InterviewSlotRepository interviewSlotRepository;

    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    InterviewerRepository interviewerRepository;

    private Logger logger = Logger.getLogger(InterviewSlotService.class.getName());

    public InterviewSlot create(InterviewSlotDTO interviewSlotDTO) throws BadRequestException, PastDateException, UserNotFoundException, ConflictException{

        if (interviewSlotDTO.getTime()==null || interviewSlotDTO.getInterviewer()==null){
            logger.log(Level.WARNING, "Time and Interview Required to create InterviewSlot instance!");
            throw new BadRequestException("Slot needs a set Time and an Interviewer!");
        }
        LocalDateTime now = LocalDateTime.now();
        if (interviewSlotDTO.getTime().isBefore(now)){
            logger.log(Level.WARNING, "Creating Slots is not possible with past dates!");
            throw new PastDateException("Creating Slots is not possible with past dates!");
        }

        Interviewer interviewer = getInterviewerFromDB(interviewSlotDTO.getInterviewer());
        Optional<InterviewSlot> optionalOcuppiedSlot = interviewSlotRepository.findByTimeAndInterviewer(interviewSlotDTO.getTime(), interviewer);
        if (optionalOcuppiedSlot.isPresent()){
            logger.log(Level.WARNING, "Slot Already Exists!");
            throw new ConflictException("Slot Already Exists!");
        }

        InterviewSlot interviewSlotToSave = new InterviewSlot();
        interviewSlotToSave.setTime(interviewSlotDTO.getTime());
        interviewSlotToSave.setInterviewer(interviewer);

        interviewSlotRepository.save(interviewSlotToSave);

        logger.log(Level.INFO, "Interview Slot created!");

        return interviewSlotToSave;
    }

    public InterviewSlot get(Long id){
        Optional<InterviewSlot> optionalInterviewSlot = interviewSlotRepository.findById(id);
        if (optionalInterviewSlot.isEmpty()){
            logger.log(Level.INFO, "Interview Slot not found!");
            return null;
        }
        logger.log(Level.INFO, "Interview Slot found: @{0}", optionalInterviewSlot.get().getTime());
        return optionalInterviewSlot.get();
    }

    public List<InterviewSlot> getAll(){
        return interviewSlotRepository.findAll();
    }

    public InterviewSlot assignToCandidate(Long interviewSlotId, Long candidateId) throws UserNotFoundException, BadRequestException, UnavailableSlotException, ConflictException{
        Optional<InterviewSlot> optionalSlotFromDB = interviewSlotRepository.findById(interviewSlotId);
        if (optionalSlotFromDB.isEmpty()){
            logger.log(Level.INFO, "Slot not found");
            throw new BadRequestException("Slot not found!");
        }
        InterviewSlot slotFromDB = optionalSlotFromDB.get();
        if (slotFromDB.getCandidate()!=null){
            logger.log(Level.INFO, "Slot already assigned!");
            throw new UnavailableSlotException("Slot assigned to a Candidate!");
        }

        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findById(candidateId);
        if (optionalCandidateFromDB.isEmpty()){
            logger.log(Level.INFO, "Assign Slot to Existing Candidate");
            throw new UserNotFoundException("Candidate not found!");
        }
        Candidate candidateFromDB = optionalCandidateFromDB.get();
        if (candidateFromDB.getSlot()!=null){
            logger.log(Level.INFO, "Candidate already has Slot Assigned!");
            throw new ConflictException("Candidate already has Slot Assigned!");
        }

        slotFromDB.setCandidate(candidateFromDB);
        interviewSlotRepository.save(slotFromDB);
        return slotFromDB;
    }

    public List<InterviewSlot> getAvailableSlots(){
        return interviewSlotRepository.findAvailableSlots();
    }

    public List<InterviewSlot> getAvailableSlotsByTime(LocalDateTime time){
        return interviewSlotRepository.findAvailableSlotsByTime(time);
    }

    /* --- HELPER --- */
    private Interviewer getInterviewerFromDB(InterviewerDTO interviewerDTO) throws UserNotFoundException{
        Optional<Interviewer> optionalInterviewer = interviewerRepository.findByEmail(interviewerDTO.getEmail());
        if (optionalInterviewer.isEmpty()){
            logger.log(Level.INFO, "Interviewer not Found");
            throw new UserNotFoundException("Interviewer not found!");
        }
        return optionalInterviewer.get();
    }
    
}
