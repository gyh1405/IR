package com.example.demo.controller;


import java.util.List;

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
    public List<Document> searchDocuments(@RequestParam String query) {
        return luceneSearchService.searchDocuments(query);
    }
}
