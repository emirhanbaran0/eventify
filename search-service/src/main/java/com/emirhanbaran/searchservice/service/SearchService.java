package com.emirhanbaran.searchservice.service;

import com.emirhanbaran.searchservice.document.EventDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<EventDocument> searchEvents(String query, String city) {
        Criteria criteria = new Criteria();

        if (query != null && !query.isEmpty()) {
            criteria = criteria.subCriteria(
                    new Criteria("title").matches(query)
                            .or("description").matches(query)
            );
        }

        if (city != null && !city.isEmpty()) {
            criteria = criteria.and("city").is(city);
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        SearchHits<EventDocument> searchHits = elasticsearchOperations.search(criteriaQuery, EventDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}