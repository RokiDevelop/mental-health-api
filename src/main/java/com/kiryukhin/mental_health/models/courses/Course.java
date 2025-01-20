package com.kiryukhin.mental_health.models.courses;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "price", precision = 20, scale = 2)
    private BigDecimal price;

    @Column(name = "price_with_discount", precision = 20, scale = 2)
    private BigDecimal priceWithDiscount;

    @Column(name = "is_discounted")
    private boolean isDiscounted = false;

    @Column(name = "is_published")
    private boolean isPublished = false;

    @Column(name = "image_preview_url", length = 1024)
    private String imagePreviewUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedDateTime;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;
}
