package com.kiryukhin.mental_health.models.meetings;

import com.kiryukhin.mental_health.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "individual_consultations")
public class IndividualConsultation extends Meeting {
    @Column(name = "is_reserved")
    private boolean isReserved;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
}
