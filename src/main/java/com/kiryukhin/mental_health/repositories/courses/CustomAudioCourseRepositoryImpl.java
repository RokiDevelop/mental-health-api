package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.AudioCourse;
import com.kiryukhin.mental_health.models.courses.QAudioCourse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class CustomAudioCourseRepositoryImpl implements CustomAudioCourseRepository<AudioCourse> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AudioCourse> findByKeywords(Pageable pageable, List<String> keywords) {
        JPAQuery<AudioCourse> query = new JPAQuery<>(entityManager);
        QAudioCourse audioCourse = QAudioCourse.audioCourse;

        BooleanBuilder builder = new BooleanBuilder();
        for (String key : keywords) {
            String lowerKey = "%" + key.toLowerCase() + "%";
            builder.or(audioCourse.title.toLowerCase().like(lowerKey))
                    .or(audioCourse.details.toLowerCase().like(lowerKey))
                    .or(audioCourse.description.toLowerCase().like(lowerKey));
        }

        List<? extends OrderSpecifier<? extends Serializable>> orderSpecifiers =
                createOrderSpecifiers(pageable, audioCourse);

        QueryResults<AudioCourse> queryResults = query.select(audioCourse)
                .from(audioCourse)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .fetchResults();

        return new PageImpl<>(
                queryResults.getResults(),
                pageable,
                queryResults.getTotal()
        );
    }

    @Override
    public Page<AudioCourse> findByKeywordsAndIsPublishedTrue(Pageable pageable, List<String> keywords) {
        JPAQuery<AudioCourse> query = new JPAQuery<>(entityManager);
        QAudioCourse audioCourse = QAudioCourse.audioCourse;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(audioCourse.isPublished.isTrue());

        if (keywords != null && !keywords.isEmpty()) {
            BooleanBuilder keywordsBuilder = new BooleanBuilder();
            for (String key : keywords) {
                String lowerKey = "%" + key.toLowerCase() + "%";
                keywordsBuilder.or(audioCourse.title.toLowerCase().like(lowerKey))
                        .or(audioCourse.details.toLowerCase().like(lowerKey))
                        .or(audioCourse.description.toLowerCase().like(lowerKey));
            }
            builder.and(keywordsBuilder);
        }

        List<? extends OrderSpecifier<? extends Serializable>> orderSpecifiers =
                createOrderSpecifiers(pageable, audioCourse);

        QueryResults<AudioCourse> queryResults = query.select(audioCourse)
                .from(audioCourse)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .fetchResults();

        return new PageImpl<>(
                queryResults.getResults(),
                pageable,
                queryResults.getTotal()
        );
    }

    private List<? extends OrderSpecifier<? extends Serializable>> createOrderSpecifiers(Pageable pageable, QAudioCourse audioCourse){
        return pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    if ("title".equals(property)) {
                        return order.isAscending() ? audioCourse.title.asc() : audioCourse.title.desc();
                    } else if ("price".equals(property)) {
                        return order.isAscending() ? audioCourse.price.asc() : audioCourse.price.desc();
                    } else if ("priceWithDiscount".equals(property)) {
                        return order.isAscending() ? audioCourse.priceWithDiscount.asc() : audioCourse.priceWithDiscount.desc();
                    } else if ("discounted".equals(property)) {
                        return order.isAscending() ? audioCourse.isDiscounted.asc() : audioCourse.isDiscounted.desc();
                    } else if ("createdDateTime".equals(property)) {
                        return order.isAscending() ? audioCourse.createdDateTime.asc() : audioCourse.createdDateTime.desc();
                    }
                    throw new IllegalArgumentException("Unknown property for sorting: " + property);
                })
                .toList();
    }
}
