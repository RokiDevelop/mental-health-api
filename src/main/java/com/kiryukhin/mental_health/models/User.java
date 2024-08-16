package com.kiryukhin.mental_health.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "USERS")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @Column(nullable = false)
    private String firstName = "Name";

    @Column(nullable = false)
    private String lastName = "Surname";

    @Column(nullable = true)
    private LocalDateTime birthday;

    @Column(nullable = false)
    private Boolean isSuperuser = false;
}