package com.kiryukhin.mental_health.models.courses;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "course_topics")
public class CourseTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 50, name = "name")
    private String name;

    @OneToMany(mappedBy = "topic")
    private Set<AudioCourse> audioCourses = new HashSet<>();

    @OneToMany(mappedBy = "topic")
    private Set<VideoCourse> videoCourses = new HashSet<>();
}
