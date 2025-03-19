package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IndividualConsultationRepository
        extends JpaRepository<IndividualConsultation, UUID>,
        CommonMeetingRepository<IndividualConsultation>,
        CustomIndividualConsultationRepository<IndividualConsultation> {

    @Query("SELECT ic FROM IndividualConsultation ic " +
            "WHERE ic.isVisible = true AND ic.isReserved = false " +
            "AND (LOWER(ic.details) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ic.description) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "OR LOWER(ic.title) LIKE LOWER(CONCAT('%', :filter, '%')))")
    Page<IndividualConsultation> findAllByIsVisibleTrueAndIsReservedFalseAndByContainingFilters(
            Pageable pageable, @Param("filter") String filter);

    @Override
    Optional<IndividualConsultation> findById(UUID uuid);

    @Override
    void deleteById(UUID uuid);

    Page<IndividualConsultation> findAllByIsVisibleTrueAndIsReservedFalse(Pageable pageable);
}
