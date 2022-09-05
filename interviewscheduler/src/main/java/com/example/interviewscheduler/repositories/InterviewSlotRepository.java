package com.example.interviewscheduler.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.interviewscheduler.models.InterviewSlot;
import com.example.interviewscheduler.models.Interviewer;

@Repository
public interface InterviewSlotRepository extends JpaRepository<InterviewSlot, Long>{
    Optional<InterviewSlot> findByTimeAndInterviewer(LocalDateTime time, Interviewer interviewer);
    @Query("SELECT s FROM InterviewSlot s WHERE s.candidate IS NULL")
    List<InterviewSlot> findAvailableSlots();
    @Query(value="SELECT s FROM InterviewSlot s WHERE s.time = ?1 AND s.candidate IS NULL")
    List<InterviewSlot> findAvailableSlotsByTime(LocalDateTime time);
}
