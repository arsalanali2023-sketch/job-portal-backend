package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private Double salary;
    private String experienceRequired;
    private String skillsRequired;
    private boolean active;
    private String postedByName;
    private String postedByEmail;
    private LocalDateTime postedAt;
}