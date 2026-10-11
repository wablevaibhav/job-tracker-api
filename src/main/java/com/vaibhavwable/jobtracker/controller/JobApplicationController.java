package com.vaibhavwable.jobtracker.controller;

import com.vaibhavwable.jobtracker.dto.ApplicationDtos.ApplicationRequest;
import com.vaibhavwable.jobtracker.dto.ApplicationDtos.ApplicationResponse;
import com.vaibhavwable.jobtracker.dto.ApplicationDtos.StatsResponse;
import com.vaibhavwable.jobtracker.dto.ApplicationDtos.StatusUpdateRequest;
import com.vaibhavwable.jobtracker.model.ApplicationStatus;
import com.vaibhavwable.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private static final int MAX_PAGE_SIZE = 100;

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(Authentication auth, @Valid @RequestBody ApplicationRequest request) {
        return service.create(auth.getName(), request);
    }

    @GetMapping
    public Page<ApplicationResponse> list(Authentication auth,
                                          @RequestParam(required = false) ApplicationStatus status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        PageRequest pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return service.list(auth.getName(), status, pageable);
    }

    @GetMapping("/stats")
    public StatsResponse stats(Authentication auth) {
        return service.stats(auth.getName());
    }

    @GetMapping("/{id}")
    public ApplicationResponse get(Authentication auth, @PathVariable Long id) {
        return service.get(auth.getName(), id);
    }

    @PutMapping("/{id}")
    public ApplicationResponse update(Authentication auth, @PathVariable Long id,
                                      @Valid @RequestBody ApplicationRequest request) {
        return service.update(auth.getName(), id, request);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(Authentication auth, @PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(auth.getName(), id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Long id) {
        service.delete(auth.getName(), id);
    }
}
