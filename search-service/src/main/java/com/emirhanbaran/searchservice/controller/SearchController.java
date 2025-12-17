package com.emirhanbaran.searchservice.controller;

import com.emirhanbaran.searchservice.document.EventDocument;
import com.emirhanbaran.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public List<EventDocument> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city
    ) {
        return searchService.searchEvents(query, city);
    }
}