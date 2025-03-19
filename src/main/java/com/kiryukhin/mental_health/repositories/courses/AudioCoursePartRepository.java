package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.AudioCoursePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AudioCoursePartRepository extends JpaRepository<AudioCoursePart, Long> {
    List<AudioCoursePart> findByAudioCourseOrderByOrderIndex(AudioCourse audioCourse);

    @Query("SELECT MAX(acp.orderIndex) FROM AudioCoursePart acp WHERE acp.audioCourse = ?1")
    Optional<Integer> findMaxOrderIndexByAudioCourse(AudioCourse audioCourse);

    @Modifying
    @Query(value = """
                UPDATE audio_course_parts 
                SET order_index = null 
                WHERE id = :partId1;
            
                UPDATE audio_course_parts 
                SET order_index = 
                    CASE 
                        WHEN id = :partId1 THEN :orderIndex2 
                        WHEN id = :partId2 THEN :orderIndex1 
                    END
                WHERE id IN (:partId1, :partId2);
            """, nativeQuery = true)
    void swapOrderIndexes(@Param("partId1") Long partId1,
                          @Param("orderIndex1") int orderIndex1,
                          @Param("partId2") Long partId2,
                          @Param("orderIndex2") int orderIndex2);
}
