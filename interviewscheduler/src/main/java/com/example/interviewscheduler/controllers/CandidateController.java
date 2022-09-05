package com.example.interviewscheduler.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interviewscheduler.dtos.CandidateDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.Candidate;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.services.CandidateService;

@RestController
@RequestMapping("api/scheduler/candidate")
@CrossOrigin
public class CandidateController {

    @Autowired
    CandidateService candidateService;
    
    @PostMapping("/register")
    public ResponseEntity<Candidate> register( @Valid @RequestBody CandidateDTO candidateDTO ) {
        Candidate candidateSaved;
        try {
          candidateSaved = candidateService.register( candidateDTO );
        } catch (DuplicatedUserException e) {
          return ResponseEntity.status( HttpStatus.CONFLICT ).body( null );
        }
        return ResponseEntity.status( HttpStatus.CREATED ).body( candidateSaved );
    }

    @GetMapping("")
    public ResponseEntity<List<Candidate>> getAll() {
        return ResponseEntity.status( HttpStatus.OK ).body( candidateService.getAllCandidates() );
    }

    @PostMapping("/{email}")
    public ResponseEntity<Candidate> login( @PathVariable @Valid @Email String email ) {
        Candidate candidate = candidateService.getCandidateByEmail( email );
        if (candidate==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( candidate );
    }

    @GetMapping("interviewslot/{id}")
    public ResponseEntity<InterviewSlot> getInterviewSlot( @PathVariable @Valid Long id ) {
        InterviewSlot interviewSlot;
        try {
            interviewSlot = candidateService.getInterviewSlotByCandidateId( id );
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( interviewSlot );
    }
    
}
