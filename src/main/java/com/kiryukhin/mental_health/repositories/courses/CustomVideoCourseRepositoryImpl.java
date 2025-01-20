package com.kiryukhin.mental_health.repositories.courses;

import com.kiryukhin.mental_health.models.courses.QAudioCourse;
import com.kiryukhin.mental_health.models.courses.QVideoCourse;
import com.kiryukhin.mental_health.models.courses.VideoCourse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomVideoCourseRepositoryImpl implements CustomVideoCourseRepository<VideoCourse>{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<VideoCourse> findByKeywords(Pageable pageable, List<String> keywords) {
        JPAQuery<VideoCourse> query = new JPAQuery<>(entityManager);
        QVideoCourse videoCourse = QVideoCourse.videoCourse;

        BooleanBuilder builder = new BooleanBuilder();
        for (String key : keywords) {
            String lowerKey = "%" + key.toLowerCase() + "%";
            builder.or(videoCourse.title.toLowerCase().like(lowerKey))
                    .or(videoCourse.details.toLowerCase().like(lowerKey))
                    .or(videoCourse.description.toLowerCase().like(lowerKey));
        }

        List<? extends OrderSpecifier<? extends Serializable>> orderSpecifiers =
                createOrderSpecifiers(pageable, videoCourse);

        QueryResults<VideoCourse> queryResults = query.select(videoCourse)
                .from(videoCourse)
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
    public Page<VideoCourse> findByKeywordsAndIsPublishedTrue(Pageable pageable, List<String> keywords) {
        JPAQuery<VideoCourse> query = new JPAQuery<>(entityManager);
        QVideoCourse videoCourse = QVideoCourse.videoCourse;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(videoCourse.isPublished.isTrue());

        if (keywords != null && !keywords.isEmpty()) {
            BooleanBuilder keywordsBuilder = new BooleanBuilder();
            for (String key : keywords) {
                String lowerKey = "%" + key.toLowerCase() + "%";
                keywordsBuilder.or(videoCourse.title.toLowerCase().like(lowerKey))
                        .or(videoCourse.details.toLowerCase().like(lowerKey))
                        .or(videoCourse.description.toLowerCase().like(lowerKey));
            }
            builder.and(keywordsBuilder);
        }

        List<? extends OrderSpecifier<? extends Serializable>> orderSpecifiers =
                createOrderSpecifiers(pageable, videoCourse);

        QueryResults<VideoCourse> queryResults = query.select(videoCourse)
                .from(videoCourse)
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

    private List<? extends OrderSpecifier<? extends Serializable>> createOrderSpecifiers(Pageable pageable, QVideoCourse videoCourse){
        return pageable.getSort().stream()
                .map(order -> {
                    String property = order.getProperty();
                    if ("title".equals(property)) {
                        return order.isAscending() ? videoCourse.title.asc() : videoCourse.title.desc();
                    } else if ("price".equals(property)) {
                        return order.isAscending() ? videoCourse.price.asc() : videoCourse.price.desc();
                    } else if ("priceWithDiscount".equals(property)) {
                        return order.isAscending() ? videoCourse.priceWithDiscount.asc() : videoCourse.priceWithDiscount.desc();
                    } else if ("discounted".equals(property)) {
                        return order.isAscending() ? videoCourse.isDiscounted.asc() : videoCourse.isDiscounted.desc();
                    } else if ("createdDateTime".equals(property)) {
                        return order.isAscending() ? videoCourse.createdDateTime.asc() : videoCourse.createdDateTime.desc();
                    }
                    throw new IllegalArgumentException("Unknown property for sorting: " + property);
                })
                .toList();
    }
}
