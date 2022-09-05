package com.example.interviewscheduler.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interviewscheduler.models.Interviewer;

@Repository
public interface InterviewerRepository extends JpaRepository<Interviewer, Long>{
    
    Optional<Interviewer> findByEmail(String email);
    
}
