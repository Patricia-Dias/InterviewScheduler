package com.example.interviewscheduler.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerDTO {

    @NotNull(message = "Interviewer name is required")
    @NotEmpty
    private String name;

    @NotNull(message = "Interviewer email is required")
    @Email
    private String email;
    
}
