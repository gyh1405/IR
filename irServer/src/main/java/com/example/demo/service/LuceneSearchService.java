package com.example.demo.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class LuceneSearchService {
    // Define instance variables for IndexWriter, IndexSearcher, Analyzer, etc.
	private final Analyzer analyzer;
    private final Directory index;
    public LuceneSearchService(@Value("${lucene.index.directory}") String indexDirectory) {
        analyzer = new StandardAnalyzer();
        Path indexPath = Paths.get(indexDirectory);
        try {
            index = FSDirectory.open(indexPath);
            new IndexWriterConfig(analyzer);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Lucene index", e);
        }
    }

    
    public List<Document> searchDocuments(String queryString) {
        try (DirectoryReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser queryParser = new QueryParser("content", analyzer);
            Query query;
            try {
                query = queryParser.parse(queryString);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid query string", e);
            }
            TopDocs topDocs;
            try {
                topDocs = searcher.search(query, 10);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to execute search query", e);
            }
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            List<Document> results = new ArrayList<>();
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docId = scoreDoc.doc;
                try {
                    Document document = searcher.doc(docId);
                    results.add(document);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to retrieve document", e);
                }
            }
            return results;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to open index for searching", e);
        }
    }

}
