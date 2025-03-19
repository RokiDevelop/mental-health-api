package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.VideoCourse;
import com.kiryukhin.mental_health.models.courses.VideoCoursePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoCoursePartRepository extends JpaRepository<VideoCoursePart, Long> {
    @Query("SELECT MAX(vcp.orderIndex) FROM VideoCoursePart vcp WHERE vcp.videoCourse = :videoCourse")
    Optional<Integer> findMaxOrderIndexByVideoCourse(@Param("videoCourse") VideoCourse videoCourse);

    List<VideoCoursePart> findByVideoCourseOrderByOrderIndex(VideoCourse videoCourse);

    @Modifying
    @Query(value = """
                UPDATE video_course_parts 
                SET order_index = null 
                WHERE id = :partId1;
            
                UPDATE video_course_parts 
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
