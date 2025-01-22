package com.kiryukhin.mental_health.models.meetings;

import com.kiryukhin.mental_health.models.User;
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
@Table(name = "group_practices")
public class GroupPractice extends Meeting {
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinTable(
            name = "group_practice2user",
            joinColumns = @JoinColumn(name = "group_practice_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<User> users = new HashSet<>();

    @Column(name = "image_preview_url", length = 1024)
    private String imagePreviewUrl;
}
