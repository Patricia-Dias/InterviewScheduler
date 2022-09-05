package com.example.interviewscheduler.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interviewscheduler.models.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>{

    Optional<Candidate> findByEmail(String email);
    
}
