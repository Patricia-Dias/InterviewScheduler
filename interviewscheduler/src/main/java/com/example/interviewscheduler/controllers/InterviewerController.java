package com.example.interviewscheduler.controllers;

import java.util.List;
import java.util.Set;

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

import com.example.interviewscheduler.dtos.InterviewerDTO;
import com.example.interviewscheduler.exceptions.DuplicatedUserException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.Interviewer;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.services.InterviewerService;

@RestController
@RequestMapping("api/scheduler/interviewer")
@CrossOrigin
public class InterviewerController {

    @Autowired
    InterviewerService interviewerService;
    
    @PostMapping("/register")
    public ResponseEntity<Interviewer> register( @Valid @RequestBody InterviewerDTO interviewerDTO ) {
        Interviewer interviewerSaved;
        try {
          interviewerSaved = interviewerService.register( interviewerDTO );
        } catch (DuplicatedUserException e) {
          return ResponseEntity.status( HttpStatus.CONFLICT ).body( null );
        }
        return ResponseEntity.status( HttpStatus.CREATED ).body( interviewerSaved );
    }

    @GetMapping("")
    public ResponseEntity<List<Interviewer>> getAll() {
        return ResponseEntity.status( HttpStatus.OK ).body( interviewerService.getAllInterviewers() );
    }

    @PostMapping("/{email}")
    public ResponseEntity<Interviewer> login( @PathVariable @Valid @Email String email ) {
        Interviewer interviewer = interviewerService.getInterviewerByEmail( email );
        if (interviewer==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( interviewer );
    }

    @GetMapping("interviewslot/{id}")
    public ResponseEntity<Set<InterviewSlot>> getInterviewSlot( @PathVariable @Valid Long id ) {
        Set<InterviewSlot> setOfInterviewSlot;
        try {
            setOfInterviewSlot = interviewerService.getAllInterviewSlotsByInterviewer( id );
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( setOfInterviewSlot );
    }
    
}
