package com.kiryukhin.mental_health.models.courses;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "video_courses")
public class VideoCourse extends Course {

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE)
    @JoinTable(
            name = "video_course_tags",
            joinColumns = @JoinColumn(name = "video_course_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_tag_id", referencedColumnName = "id")
    )
    private Set<CourseTag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "topic_id")
    private CourseTopic topic;
}
