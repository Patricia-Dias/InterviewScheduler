package com.example.interviewscheduler.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.interviewscheduler.dtos.InterviewSlotDTO;
import com.example.interviewscheduler.exceptions.BadRequestException;
import com.example.interviewscheduler.exceptions.ConflictException;
import com.example.interviewscheduler.exceptions.PastDateException;
import com.example.interviewscheduler.exceptions.UnavailableSlotException;
import com.example.interviewscheduler.exceptions.UserNotFoundException;
import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.services.InterviewSlotService;

@RestController
@RequestMapping("api/scheduler/interviewslot")
@CrossOrigin
public class InterviewSlotController {

    @Autowired
    InterviewSlotService interviewSlotService;

    @PostMapping("")
    public ResponseEntity<InterviewSlot> createSlot( @Valid @RequestBody InterviewSlotDTO interviewSlotDTO ) throws ConflictException {
        InterviewSlot interviewSlot;
        try {
            interviewSlot = interviewSlotService.create( interviewSlotDTO );
        } catch (BadRequestException|PastDateException e) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body(null);
        } catch (UserNotFoundException e){
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body(null);
        } catch (ConflictException e){
            return ResponseEntity.status( HttpStatus.CONFLICT ).body(null);
        }
        return ResponseEntity.status( HttpStatus.CREATED ).body( interviewSlot );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewSlot> getSlotById( @PathVariable @Valid Long id){
        InterviewSlot interviewSlot = interviewSlotService.get( id );
        if (interviewSlot==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( interviewSlot );
    }

    @GetMapping("")
    public ResponseEntity<List<InterviewSlot>> getAllSlots(){
        return ResponseEntity.status( HttpStatus.OK ).body( interviewSlotService.getAll() );
    }

    @GetMapping("/available")
    public ResponseEntity<List<InterviewSlot>> getAvailableSlots(){
        return ResponseEntity.status( HttpStatus.OK ).body( interviewSlotService.getAvailableSlots() );
    }

    @GetMapping("/availablebytime")
    public ResponseEntity<InterviewSlot> getAvailableSlotByTime(@RequestParam(required = true) @Valid String time){
        LocalDateTime dateTime = LocalDateTime.parse(time);
        List<InterviewSlot> interviewSlots = interviewSlotService.getAvailableSlotsByTime(dateTime);
        if (interviewSlots.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( interviewSlots.get(0) );
    }

    @PutMapping("")
    public ResponseEntity<InterviewSlot> assignSlotToCandidate(@RequestParam(required = true) Long slotId, @RequestParam(required = true) Long candidateId) {
        InterviewSlot newInterviewSlot;
        try {
            newInterviewSlot = interviewSlotService.assignToCandidate(slotId, candidateId);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (UnavailableSlotException|ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.status( HttpStatus.OK ).body( newInterviewSlot );
    }
    
}
