package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.GroupPractice;
import com.kiryukhin.mental_health.models.meetings.QGroupPractice;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomGroupPracticeRepositoryImpl implements CustomGroupPracticeRepository<GroupPractice> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<GroupPractice> findByKeywords(Pageable pageable, List<String> keywords) {
        JPAQuery<GroupPractice> query = new JPAQuery<>(entityManager);
        QGroupPractice groupPractice = QGroupPractice.groupPractice;

        BooleanBuilder builder = new BooleanBuilder();
        for (String key : keywords) {
            String lowerKey = "%" + key.toLowerCase() + "%";
            builder.or(groupPractice.title.toLowerCase().like(lowerKey))
                    .or(groupPractice.description.toLowerCase().like(lowerKey))
                    .or(groupPractice.details.toLowerCase().like(lowerKey));
        }

        QueryResults<GroupPractice> queryResults = query.select(groupPractice)
                .from(groupPractice)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return queryResults.getResults() == null || queryResults.getResults().isEmpty()
                ? Page.empty(pageable)
                : new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public Page<GroupPractice> findByKeywordsAndIsVisibleTrue(Pageable pageable, List<String> keywords) {
        JPAQuery<GroupPractice> query = new JPAQuery<>(entityManager);
        QGroupPractice groupPractice = QGroupPractice.groupPractice;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(groupPractice.isVisible.isTrue());

        if (keywords != null && !keywords.isEmpty()) {
            BooleanBuilder keywordsBuilder = new BooleanBuilder();
            for (String key : keywords) {
                String lowerKey = "%" + key.toLowerCase() + "%";
                keywordsBuilder.or(groupPractice.title.toLowerCase().like(lowerKey))
                        .or(groupPractice.description.toLowerCase().like(lowerKey))
                        .or(groupPractice.details.toLowerCase().like(lowerKey));
            }
            builder.and(keywordsBuilder);
        }

        QueryResults<GroupPractice> queryResults = query.select(groupPractice)
                .from(groupPractice)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return queryResults.getResults() == null || queryResults.getResults().isEmpty()
                ? Page.empty(pageable)
                : new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
