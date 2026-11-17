package com.jobportal.service;

import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(JobRequest request, String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .salary(request.getSalary())
                .experienceRequired(request.getExperienceRequired())
                .skillsRequired(request.getSkillsRequired())
                .active(true)
                .postedBy(recruiter)
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToResponse(savedJob);
    }

    public List<JobResponse> getAllActiveJobs() {
        return jobRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return mapToResponse(job);
    }

    public List<JobResponse> searchJobs(String keyword, String location) {
        return jobRepository.searchJobs(keyword, location)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getMyPostedJobs(String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found"));

        return jobRepository.findByPostedById(recruiter.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobResponse updateJob(Long jobId, JobRequest request, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(recruiterEmail)) {
            throw new AccessDeniedException("You can only update your own job postings");
        }

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalary(request.getSalary());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setSkillsRequired(request.getSkillsRequired());

        return mapToResponse(jobRepository.save(job));
    }

    public String deleteJob(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (!job.getPostedBy().getEmail().equals(recruiterEmail)) {
            throw new AccessDeniedException("You can only delete your own job postings");
        }

        job.setActive(false);
        jobRepository.save(job);
        return "Job closed successfully";
    }

    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .salary(job.getSalary())
                .experienceRequired(job.getExperienceRequired())
                .skillsRequired(job.getSkillsRequired())
                .active(job.isActive())
                .postedByName(job.getPostedBy().getFullName())
                .postedByEmail(job.getPostedBy().getEmail())
                .postedAt(job.getPostedAt())
                .build();
    }
}