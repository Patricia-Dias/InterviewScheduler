package com.example.interviewscheduler.dtos;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSlotDTO {

    @NotNull(message = "Slot date/time is required")
    @NotEmpty
    private LocalDateTime time;

    @NotNull(message = "Slot Interviewer is required")
    @NotEmpty
    private InterviewerDTO interviewer;
    
}
