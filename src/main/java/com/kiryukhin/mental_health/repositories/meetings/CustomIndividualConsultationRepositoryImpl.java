package com.kiryukhin.mental_health.repositories.meetings;

import com.kiryukhin.mental_health.models.meetings.IndividualConsultation;
import com.kiryukhin.mental_health.models.meetings.QIndividualConsultation;
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
public class CustomIndividualConsultationRepositoryImpl implements CustomIndividualConsultationRepository<IndividualConsultation> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<IndividualConsultation> findByKeywords(Pageable pageable, List<String> keywords) {
        JPAQuery<IndividualConsultation> query = new JPAQuery<>(entityManager);
        QIndividualConsultation individualConsultation = QIndividualConsultation.individualConsultation;

        BooleanBuilder builder = new BooleanBuilder();

        keywords.stream()
                .map(key -> "%" + key.toLowerCase() + "%")
                .forEach(lowerKey -> builder.or(individualConsultation.title.likeIgnoreCase(lowerKey))
                        .or(individualConsultation.description.likeIgnoreCase(lowerKey))
                        .or(individualConsultation.details.likeIgnoreCase(lowerKey)));

        QueryResults<IndividualConsultation> queryResults = query.select(individualConsultation)
                .from(individualConsultation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return queryResults.getResults() == null || queryResults.getResults().isEmpty()
                ? Page.empty(pageable)
                : new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    @Override
    public Page<IndividualConsultation> findByKeywordsAndIsVisibleTrueAndIsReservedFalse(Pageable pageable, List<String> keywords) {
        JPAQuery<IndividualConsultation> query = new JPAQuery<>(entityManager);
        QIndividualConsultation individualConsultation = QIndividualConsultation.individualConsultation;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(individualConsultation.isReserved.isFalse())
                .and(individualConsultation.isVisible.isTrue());

        if (keywords != null && !keywords.isEmpty()) {
            BooleanBuilder keywordsBuilder = new BooleanBuilder();
            keywords.stream()
                    .map(key -> "%" + key.toLowerCase() + "%")
                    .forEach(lowerKey -> keywordsBuilder.or(individualConsultation.title.likeIgnoreCase(lowerKey))
                            .or(individualConsultation.description.likeIgnoreCase(lowerKey))
                            .or(individualConsultation.details.likeIgnoreCase(lowerKey)));
            builder.and(keywordsBuilder);
        }



        QueryResults<IndividualConsultation> queryResults = query.select(individualConsultation)
                .from(individualConsultation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return queryResults.getResults() == null || queryResults.getResults().isEmpty()
                ? Page.empty(pageable)
                : new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }


//    public Page<IndividualConsultation> findByKeywordsAndIsVisibleTrueAndIsReservedFalsePage(Pageable pageable, List<String> keywords) {
//        if (keywords == null || keywords.isEmpty()) {
//            return Page.empty(pageable);
//        }
//
//        JPAQuery<IndividualConsultation> query = new JPAQuery<>(entityManager);
//        QIndividualConsultation individualConsultation = QIndividualConsultation.individualConsultation;
//
//        // Фильтры по isVisible и isReserved
//        BooleanBuilder builder = new BooleanBuilder()
//                .and(individualConsultation.isVisible)
//                .andNot(individualConsultation.isReserved);
//
//        // Генерация tsquery
//        String tsQuery = keywords.stream()
//                .map(key -> key.replaceAll("'", "''")) // Экранирование для SQL
//                .reduce((a, b) -> a + " | " + b) // Объединение ключевых слов через OR
//                .orElse("");
//
//        builder.and(Expressions.booleanTemplate(
//                "to_tsvector('english', {0}) @@ to_tsquery('english', {1})",
//                individualConsultation.searchVector,
//                tsQuery
//        ));
//
//        QueryResults<IndividualConsultation> queryResults = query.select(individualConsultation)
//                .from(individualConsultation)
//                .where(builder)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//
//        List<IndividualConsultation> results = queryResults.getResults();
//        return results == null || results.isEmpty()
//                ? Page.empty(pageable)
//                : new PageImpl<>(results, pageable, queryResults.getTotal());
//    }
}
