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
public class CandidateDTO {

    @NotNull(message = "Candidate name is required")
    @NotEmpty
    private String name;

    @NotNull(message = "Candidate email is required")
    @Email
    private String email;
    
}
