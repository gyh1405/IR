package com.example.demo.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.SearchUtils;
import com.example.demo.service.LuceneSearchService;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final LuceneSearchService luceneSearchService;

    public SearchController(LuceneSearchService luceneSearchService) {
        this.luceneSearchService = luceneSearchService;
    }

    
    
    @GetMapping
    public List<String> searchDocuments(@RequestParam String query) throws IOException, ParseException {
    	
    	List<String> titleAndScoreList = new ArrayList<>();
    	
    	// 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. Create or open the index
        ByteBuffersDirectory index = luceneSearchService.getIndex();

        // 2. Create the index reader
        IndexReader reader = DirectoryReader.open(index);

        // 3. Create the index searcher
        IndexSearcher searcher = new IndexSearcher(reader);

       
            	
            	
        Query q = SearchUtils.createQuery(query, analyzer);

        if (q != null) {
            TopDocs docs = searcher.search(q, 50);
            ScoreDoc[] hits = docs.scoreDocs;

            // 5. Display results
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                float termScore = hits[i].score; // Get the term score
                
                
                String titleAndScore = (i + 1) + ". Title: " + d.get("title") + " \nScore: " + termScore;
                
                titleAndScoreList.add(titleAndScore);
            }
        }
    	
    	
        return titleAndScoreList;
        
        
        
        
        
    }
    
    
    
}
