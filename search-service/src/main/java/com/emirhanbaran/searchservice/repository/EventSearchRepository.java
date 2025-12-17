package com.emirhanbaran.searchservice.repository;

import com.emirhanbaran.searchservice.document.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {

}