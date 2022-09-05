package com.example.interviewscheduler.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.interviewscheduler.dtos.InterviewerDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.repositories.InterviewerRepository;

@Service
public class InterviewerService {

    @Autowired
    InterviewerRepository interviewerRepository;

    private Logger logger = Logger.getLogger(InterviewerService.class.getName());

    public Interviewer register(InterviewerDTO interviewerDTO) throws DuplicatedUserException {
        Optional<Interviewer> optionalInterviewerFromDB = interviewerRepository.findByEmail(interviewerDTO.getEmail());

        if (optionalInterviewerFromDB.isPresent()){
            logger.log(Level.INFO, "Email already in use: {0}", interviewerDTO.getEmail());
            throw new DuplicatedUserException("Interviewer Already Exists!");
        }

        Interviewer interviewerToSave = new Interviewer();
        interviewerToSave.setEmail(interviewerDTO.getEmail());
        interviewerToSave.setName(interviewerDTO.getName());

        interviewerRepository.save(interviewerToSave);

        logger.log(Level.INFO, "Interviewer with email {0} is now registered!", interviewerToSave.getEmail());

        return interviewerToSave;
    }

    public Interviewer getInterviewerById(long id) {
        Optional<Interviewer> optionalInterviewer = interviewerRepository.findById(id);
        return returnInterviewer(optionalInterviewer);
    }

    public Interviewer getInterviewerByEmail(String email) {
        Optional<Interviewer> optionalInterviewer = interviewerRepository.findByEmail(email);
        return returnInterviewer(optionalInterviewer);
    }

    public Set<InterviewSlot> getAllInterviewSlotsByInterviewer(long id) throws UserNotFoundException {
        Interviewer interviewer = getInterviewerById(id);
        if (interviewer == null){
            logger.log(Level.INFO, "Interviewer Not Found!");
            throw new UserNotFoundException("Interviewer Not Found!");
        }
        Set<InterviewSlot> interviewSlots = interviewer.getSlots();
        if (interviewSlots == null){
            logger.log(Level.INFO, "Interviewer does not have Interview Slots");
            return Set.of();
        }
        return interviewSlots;
    }

    public List<Interviewer> getAllInterviewers() {
        return interviewerRepository.findAll();
    }

    /* --- HELPER --- */
    private Interviewer returnInterviewer(Optional<Interviewer> optionalInterviewer){
        if (optionalInterviewer.isEmpty()){
            logger.log(Level.INFO, "Interviewer not found!");
            return null;
        }
        logger.log(Level.INFO, "Interviewer found: {0}", optionalInterviewer.get().getName());
        return optionalInterviewer.get();
    }
    
}
