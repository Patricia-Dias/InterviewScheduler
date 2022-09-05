package com.example.interviewscheduler.services;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.interviewscheduler.dtos.CandidateDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.repositories.CandidateRepository;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

	private Logger logger = Logger.getLogger(CandidateService.class.getName());

    public Candidate register(CandidateDTO candidateDTO) throws DuplicatedUserException {
        Optional<Candidate> optionalCandidateFromDB = candidateRepository.findByEmail(candidateDTO.getEmail());

        if (optionalCandidateFromDB.isPresent()){
            logger.log(Level.INFO, "Email already in use: {0}", candidateDTO.getEmail());
            throw new DuplicatedUserException("Candidate Already Exists!");
        }

        Candidate candidateToSave = new Candidate();
        candidateToSave.setEmail(candidateDTO.getEmail());
        candidateToSave.setName(candidateDTO.getName());

        candidateRepository.save(candidateToSave);

        logger.log(Level.INFO, "Candidate with email {0} is now registered!", candidateToSave.getEmail());

        return candidateToSave;
    }

    public Candidate getCandidateById(long id) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        return returnCandidate(optionalCandidate);
    }

    public Candidate getCandidateByEmail(String email) {
        Optional<Candidate> optionalCandidate = candidateRepository.findByEmail(email);
        return returnCandidate(optionalCandidate);
    }

    public InterviewSlot getInterviewSlotByCandidateId(long id) throws UserNotFoundException {
        Candidate candidate = getCandidateById(id);
        if (candidate == null){
            logger.log(Level.INFO, "Candidate Not Found!");
            throw new UserNotFoundException("Candidate Not Found!");
        }
        InterviewSlot interviewSlot = candidate.getSlot();
        if (interviewSlot==null){
            logger.log(Level.INFO, "Candidate does not have an Assigned Slot");
            return null;
        }
        logger.log(Level.INFO, "Candidate has an Assigned Slot");
        return interviewSlot;
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    /* --- HELPER --- */
    private Candidate returnCandidate(Optional<Candidate> optionalCandidate){
        if (optionalCandidate.isEmpty()){
            logger.log(Level.INFO, "Candidate not found!");
            return null;
        }
        logger.log(Level.INFO, "Candidate found: {0}", optionalCandidate.get().getName());
        return optionalCandidate.get();
    }
    
}
