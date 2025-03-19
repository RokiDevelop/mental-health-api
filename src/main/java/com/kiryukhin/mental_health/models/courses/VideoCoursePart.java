package com.kiryukhin.mental_health.models.courses;

import com.kiryukhin.mental_health.models.StorageItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "video_course_parts",
        uniqueConstraints = {
        @UniqueConstraint(name = "uniqueOrderAndVideoCourse", columnNames = {"order_index", "video_course_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoCoursePart extends CoursePart {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "video_course_id")
    private VideoCourse videoCourse;

    @OneToMany(mappedBy = "videoCoursePart", fetch = FetchType.LAZY)
    private List<StorageItem> storageItems = new ArrayList<>();
}