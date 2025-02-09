package com.embarkx.jobms.job.impl;

import com.embarkx.jobms.job.Job;
import com.embarkx.jobms.job.JobRepository;
import com.embarkx.jobms.job.JobService;
import com.embarkx.jobms.job.clients.CompanyClient;
import com.embarkx.jobms.job.clients.ReviewClient;
import com.embarkx.jobms.job.dto.JobDTO;
import com.embarkx.jobms.job.external.Company;
import com.embarkx.jobms.job.external.Review;
import com.embarkx.jobms.job.mapper.JobMapper;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class JobServiceImpl implements JobService {
    private JobRepository jobRepository;
    private CompanyClient companyClient;
    private ReviewClient reviewClient;

    public JobServiceImpl(JobRepository jobRepository, 
                         CompanyClient companyClient,
                         ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "findAllFallback")
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Fallback method
    public List<JobDTO> findAllFallback(Exception e) {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                  .map(job -> {
                      // Create fallback company
                      Company fallbackCompany = new Company();
                      fallbackCompany.setId(job.getCompanyId());
                      fallbackCompany.setName("Company Service Unavailable");
                      fallbackCompany.setDescription("Please try again later");
                      
                      return JobMapper.mapToJobWithCompanyDto(job, fallbackCompany, new ArrayList<>());
                  })
                  .collect(Collectors.toList());
    }

    private JobDTO convertToDto(Job job) {
        try {
            Company company = companyClient.getCompany(job.getCompanyId());
            List<Review> reviews = reviewClient.getReviews(job.getCompanyId());
            return JobMapper.mapToJobWithCompanyDto(job, company, reviews);
        } catch (Exception e) {
            System.err.println("Error fetching company or reviews: " + e.getMessage());
            e.printStackTrace();
            return JobMapper.mapToJobWithCompanyDto(job, null, null);
        }
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            return convertToDto(job);
        }
        return null;
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}