package com.example.interviewscheduler.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interview_slot")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private long id;

    @Column(name = "slot_time", nullable = false)
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name="interviewer_id", nullable = false)
    private Interviewer interviewer;

    @OneToOne
    @JoinColumn(name="candidate_id", nullable = true)
    private Candidate candidate;
    
}
