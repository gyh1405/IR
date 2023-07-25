package com.example.demo.controller;


import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.LuceneSearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final LuceneSearchService luceneSearchService;

    public SearchController(LuceneSearchService luceneSearchService) {
        this.luceneSearchService = luceneSearchService;
    }

    @GetMapping
    public List<Map<String, String>>  searchDocuments(@RequestParam String query) {
    	
    	List<Document> results = luceneSearchService.searchDocuments(query);
        
        return luceneSearchService.displayResults(results);
    }
}
