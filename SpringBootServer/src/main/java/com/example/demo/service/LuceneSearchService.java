package com.example.demo.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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

import com.example.demo.model.TestDataset;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class LuceneSearchService {

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

		// Preprocess the query
		String processedQuery = applyStopWordsRemovalAndStemmingToText(queryString);

		System.out.println("User Query (Preprocessed): " + processedQuery);

		try (DirectoryReader reader = DirectoryReader.open(index)) {

			IndexSearcher searcher = new IndexSearcher(reader);
			QueryParser queryParser = new QueryParser("content", analyzer);

			Query query;
			try {
				query = queryParser.parse(processedQuery);
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

			printSearchResults(results);

			return results;

		} catch (IOException e) {
			throw new IllegalStateException("Failed to open index for searching", e);
		}
	}

	private String applyStopWordsRemovalAndStemmingToText(String text) {
		StringBuilder processedText = new StringBuilder();
		try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
				StopFilter stopFilter = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());
				PorterStemFilter stemFilter = new PorterStemFilter(stopFilter)) {

			CharTermAttribute charTermAttribute = stemFilter.addAttribute(CharTermAttribute.class);
			stemFilter.reset();
			while (stemFilter.incrementToken()) {
				String term = charTermAttribute.toString();
				processedText.append(term).append(" ");
			}
			stemFilter.end();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to apply stop words removal and stemming", e);
		}

		return processedText.toString().trim();
	}

	private void printSearchResults(List<Document> results) {
		for (int i = 0; i < results.size(); i++) {
			Document document = results.get(i);
			System.out.println();
			System.out.println("Document " + (i + 1) + "---------");
			System.out.println("Key: " + document.get("url"));
			System.out.println("Date Scraped: " + document.get("dateTime"));
			System.out.println("Content: " + document.get("content"));
			System.out.println();
		}
	}
	
	public List<Map<String, String>> displayResults(List<Document> results) {
		

		List<Map<String, String>> displayResult = new ArrayList<>();

				
		for (int i = 0; i < results.size(); i++) {
			Document document = results.get(i);
			
			
			Map<String, String> element1 = new HashMap<>();
	        element1.put("title", document.get("originalTitle"));
	        element1.put("dateTime", document.get("dateTime"));
	        element1.put("url", document.get("url"));
	        displayResult.add(element1);
			
		}
		
		return displayResult;
	}

	public double calculatePrecisionRecall(String queryString, Map<String, Integer> relevantDocuments) {
		int totalRetrieved = 0;
		int truePositives = 0;
		int relevantDocumentsCount = relevantDocuments.size();

		List<Document> searchResults = searchDocuments(queryString);

		for (Document document : searchResults) {
			String url = document.get("url");
			totalRetrieved++;
			if (relevantDocuments.containsKey(url)) {
				truePositives++;
			}
		}

		double precision = (double) truePositives / totalRetrieved;
		double recall = (double) truePositives / relevantDocumentsCount;

		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);

		return precision;
	}

	public void evaluate() {
		// Create the test dataset
		Map<String, List<String>> testDataset = TestDataset.createTestDataset();


		// Loop through each query and its relevant documents
		for (String query : testDataset.keySet()) {
			List<String> relevantDocs = testDataset.get(query);
			Map<String, Integer> relevantDocumentsMap = new HashMap<>();
			for (String relevantDoc : relevantDocs) {
				relevantDocumentsMap.put(relevantDoc, 1);
			}

			// Calculate precision and recall
			double precision = calculatePrecisionRecall(query, relevantDocumentsMap);

			// Print the results
			System.out.println("Test Query: " + query);
			
		}

	}
}
