package com.jobportal.service;

import com.jobportal.Repository.*;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.ApplyJobRequest;
import com.jobportal.dto.StatusUpdateRequest;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.Repository.JobApplicationRepository;
import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationResponse applyForJob(Long jobId, ApplyJobRequest request, String applicantEmail) {
        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer accepting applications");
        }

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .resumeLink(request.getResumeLink())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.APPLIED)
                .build();

        return mapToResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getMyApplications(String applicantEmail) {
        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return applicationRepository.findByApplicantId(applicant.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(recruiterEmail)) {
            throw new AccessDeniedException("You can only view applications for your own jobs");
        }

        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse updateApplicationStatus(Long applicationId, StatusUpdateRequest request, String recruiterEmail) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (!application.getJob().getPostedBy().getEmail().equals(recruiterEmail)) {
            throw new AccessDeniedException("You are not authorized to update this application");
        }

        application.setStatus(request.getStatus());
        return mapToResponse(applicationRepository.save(application));
    }

    private ApplicationResponse mapToResponse(JobApplication application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .companyName(application.getJob().getCompanyName())
                .applicantName(application.getApplicant().getFullName())
                .applicantEmail(application.getApplicant().getEmail())
                .resumeLink(application.getResumeLink())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}