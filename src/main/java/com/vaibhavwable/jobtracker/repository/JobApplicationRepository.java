package com.vaibhavwable.jobtracker.repository;

import com.vaibhavwable.jobtracker.model.ApplicationStatus;
import com.vaibhavwable.jobtracker.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Page<JobApplication> findByOwnerId(Long ownerId, Pageable pageable);

    Page<JobApplication> findByOwnerIdAndStatus(Long ownerId, ApplicationStatus status, Pageable pageable);

    Optional<JobApplication> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("select a.status, count(a) from JobApplication a where a.owner.id = :ownerId group by a.status")
    List<Object[]> countByStatus(@Param("ownerId") Long ownerId);
}
