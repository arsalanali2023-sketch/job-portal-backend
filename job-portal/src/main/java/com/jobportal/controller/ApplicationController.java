package com.jobportal.controller;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.ApplyJobRequest;
import com.jobportal.dto.StatusUpdateRequest;
import com.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> applyForJob(@PathVariable Long jobId,
                                                           @Valid @RequestBody ApplyJobRequest request,
                                                           Authentication authentication) {
        return new ResponseEntity<>(
                applicationService.applyForJob(jobId, request, authentication.getName()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public List<ApplicationResponse> getMyApplications(Authentication authentication) {
        return applicationRepositoryCheck(authentication);
    }

    private List<ApplicationResponse> applicationRepositoryCheck(Authentication authentication) {
        return applicationService.getMyApplications(authentication.getName());
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationResponse> getApplicationsForJob(@PathVariable Long jobId,
                                                           Authentication authentication) {
        return applicationService.getApplicationsForJob(jobId, authentication.getName());
    }

    @PatchMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long applicationId,
                                                            @Valid @RequestBody StatusUpdateRequest request,
                                                            Authentication authentication) {
        return ResponseEntity.ok(
                applicationService.updateApplicationStatus(applicationId, request, authentication.getName())
        );
    }
}