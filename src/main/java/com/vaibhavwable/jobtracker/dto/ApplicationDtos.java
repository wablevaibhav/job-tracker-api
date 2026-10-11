package com.vaibhavwable.jobtracker.dto;

import com.vaibhavwable.jobtracker.model.ApplicationStatus;
import com.vaibhavwable.jobtracker.model.JobApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public final class ApplicationDtos {

    private ApplicationDtos() {
    }

    public record ApplicationRequest(
            @NotBlank @Size(max = 200) String company,
            @NotBlank @Size(max = 200) String role,
            @Size(max = 200) String location,
            ApplicationStatus status,
            @Size(max = 500) String jobUrl,
            @Size(max = 2000) String notes,
            LocalDate appliedOn) {
    }

    public record StatusUpdateRequest(@NotNull ApplicationStatus status) {
    }

    public record ApplicationResponse(
            Long id,
            String company,
            String role,
            String location,
            ApplicationStatus status,
            String jobUrl,
            String notes,
            LocalDate appliedOn,
            Instant updatedAt) {

        public static ApplicationResponse from(JobApplication a) {
            return new ApplicationResponse(
                    a.getId(), a.getCompany(), a.getRole(), a.getLocation(), a.getStatus(),
                    a.getJobUrl(), a.getNotes(), a.getAppliedOn(), a.getUpdatedAt());
        }
    }

    public record StatsResponse(long total, Map<ApplicationStatus, Long> byStatus) {
    }
}
