package com.vaibhavwable.jobtracker.service;

import com.vaibhavwable.jobtracker.dto.ApplicationDtos.ApplicationRequest;
import com.vaibhavwable.jobtracker.dto.ApplicationDtos.ApplicationResponse;
import com.vaibhavwable.jobtracker.dto.ApplicationDtos.StatsResponse;
import com.vaibhavwable.jobtracker.exception.ResourceNotFoundException;
import com.vaibhavwable.jobtracker.model.AppUser;
import com.vaibhavwable.jobtracker.model.ApplicationStatus;
import com.vaibhavwable.jobtracker.model.JobApplication;
import com.vaibhavwable.jobtracker.repository.JobApplicationRepository;
import com.vaibhavwable.jobtracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public ApplicationResponse create(String email, ApplicationRequest request) {
        AppUser owner = findUser(email);
        JobApplication application = new JobApplication(owner);
        apply(application, request);
        return ApplicationResponse.from(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> list(String email, ApplicationStatus status, Pageable pageable) {
        Long ownerId = findUser(email).getId();
        Page<JobApplication> page = (status == null)
                ? applicationRepository.findByOwnerId(ownerId, pageable)
                : applicationRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
        return page.map(ApplicationResponse::from);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(String email, Long id) {
        return ApplicationResponse.from(findOwned(email, id));
    }

    public ApplicationResponse update(String email, Long id, ApplicationRequest request) {
        JobApplication application = findOwned(email, id);
        apply(application, request);
        return ApplicationResponse.from(application);
    }

    public ApplicationResponse updateStatus(String email, Long id, ApplicationStatus status) {
        JobApplication application = findOwned(email, id);
        application.setStatus(status);
        return ApplicationResponse.from(application);
    }

    public void delete(String email, Long id) {
        applicationRepository.delete(findOwned(email, id));
    }

    @Transactional(readOnly = true)
    public StatsResponse stats(String email) {
        Long ownerId = findUser(email).getId();
        Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus s : ApplicationStatus.values()) {
            byStatus.put(s, 0L);
        }
        long total = 0;
        for (Object[] row : applicationRepository.countByStatus(ownerId)) {
            ApplicationStatus s = (ApplicationStatus) row[0];
            long count = (Long) row[1];
            byStatus.put(s, count);
            total += count;
        }
        return new StatsResponse(total, byStatus);
    }

    private AppUser findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private JobApplication findOwned(String email, Long id) {
        Long ownerId = findUser(email).getId();
        return applicationRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Application " + id + " not found"));
    }

    private void apply(JobApplication application, ApplicationRequest request) {
        application.setCompany(request.company().trim());
        application.setRole(request.role().trim());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setNotes(request.notes());
        if (request.status() != null) {
            application.setStatus(request.status());
        }
        if (request.appliedOn() != null) {
            application.setAppliedOn(request.appliedOn());
        }
    }
}
