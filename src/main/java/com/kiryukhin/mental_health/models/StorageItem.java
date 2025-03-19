package com.kiryukhin.mental_health.models;

import com.kiryukhin.mental_health.models.courses.AudioCoursePart;
import com.kiryukhin.mental_health.models.courses.VideoCoursePart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "storage_items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StorageItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

    @Column(name = "storage_object_id", length = 63, unique = true)
    private String storageObjectId;

    @Column(name = "url", length = 511)
    private String url;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, targetEntity = VideoCoursePart.class)
    @JoinColumn(name = "video_course_part_id")
    private VideoCoursePart videoCoursePart;

    @ManyToOne(optional = true, fetch = FetchType.EAGER, targetEntity = AudioCoursePart.class)
    @JoinColumn(name = "audio_course_part_id")
    private AudioCoursePart audioCoursePart;
}
