package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyJobRequest {

    @NotBlank(message = "Resume link is required")
    private String resumeLink;

    private String coverLetter;
}