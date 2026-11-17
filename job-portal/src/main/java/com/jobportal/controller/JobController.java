package com.jobportal.controller;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request,
                                                 Authentication authentication) {
        return new ResponseEntity<>(jobService.createJob(request, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public List<JobResponse> getAllJobs() {
        return jobService.getAllActiveJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/search")
    public List<JobResponse> searchJobs(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String location) {
        return jobService.searchJobs(keyword, location);
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<JobResponse> getMyPostedJobs(Authentication authentication) {
        return jobService.getMyPostedJobs(authentication.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id,
                                                 @Valid @RequestBody JobRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.ok(jobService.updateJob(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<String> deleteJob(@PathVariable Long id,
                                            Authentication authentication) {
        return ResponseEntity.ok(jobService.deleteJob(id, authentication.getName()));
    }
}