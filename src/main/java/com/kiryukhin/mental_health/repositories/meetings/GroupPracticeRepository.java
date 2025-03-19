package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupPracticeRepository
        extends JpaRepository<GroupPractice, UUID>,
        CommonMeetingRepository<GroupPractice>,
        CustomGroupPracticeRepository<GroupPractice>{

    @Query("SELECT ic FROM GroupPractice ic " +
            "WHERE ic.isVisible = true " +
            "AND (LOWER(ic.details) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ic.description) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ic.title) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<GroupPractice> findAllByIsVisibleTrueAndByContainingFilters(
            Pageable pageable, @Param("filter") String filter);

    Page<GroupPractice> findAllByIsVisibleTrue(Pageable pageable);

    @Override
    Optional<GroupPractice> findById(UUID uuid);
}
