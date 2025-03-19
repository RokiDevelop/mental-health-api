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
@Table(name = "audio_course_parts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniqueOrderAndAudioCourse", columnNames = {"order_index", "audio_course_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AudioCoursePart extends CoursePart {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "audio_course_id")
    private AudioCourse audioCourse;

    @OneToMany(mappedBy = "audioCoursePart", fetch = FetchType.LAZY)
    private List<StorageItem> storageItems = new ArrayList<>();
}