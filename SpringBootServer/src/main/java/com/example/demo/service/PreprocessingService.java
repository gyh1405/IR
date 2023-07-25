package com.example.demo.service;

import com.example.demo.model.ScrapedData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PreprocessingService {

	private final Analyzer analyzer;
	private final Directory index;
	private final IndexWriterConfig config;

	public PreprocessingService(@Value("${lucene.index.directory}") String indexDirectory) throws IOException {
		analyzer = new StandardAnalyzer();
		Path indexPath = Paths.get(indexDirectory);
		index = FSDirectory.open(indexPath);
		config = new IndexWriterConfig(analyzer);
	}

	public void preprocessAndIndexDataFromFiles(String jsonFilePath, String txtFilePath) {
		try {

			// Read the scraped data and preprocess it
			List<ScrapedData> scrappedDataList = readAndPreprocessJsonFile(jsonFilePath);

			// convert to document representation
			indexDocuments(createDocuments(scrappedDataList));
			System.out.println("[Preprocessed] Saved all Scraped Data.");

		} catch (IOException e) {
			throw new IllegalStateException("Failed to preprocess and index data", e);
		}
	}

//	private List<ScrapedData> readAndPreprocessJsonFile(String jsonFilePath) throws IOException {
//		ClassPathResource resource = new ClassPathResource(jsonFilePath);
//
//		// todo: to clear the return list
//
//		try (InputStream inputStream = resource.getInputStream()) {
//			ObjectMapper objectMapper = new ObjectMapper();
//			List<ScrapedData> scrappedDataList = null;
//			try {
//				scrappedDataList = objectMapper.readValue(inputStream, new TypeReference<List<ScrapedData>>() {
//				});
//			} catch (IOException e) {
//				System.err.println("Error parsing JSON line: " + e.getMessage());
//				//throw new IllegalStateException("Failed to parse JSON file", e);
//			}
//
//			// Preprocess each object in the array
//			for (ScrapedData scrappedData : scrappedDataList) {
//				String originalTitle = scrappedData.getTitle();
//				List<String> originalContent = scrappedData.getContent();
//				String processedTitle = applyStopWordsRemovalAndStemmingToText(originalTitle);
//				List<String> processedContent = new ArrayList<>();
//				for (String sentence : originalContent) {
//					String processedSentence = applyStopWordsRemovalAndStemmingToText(sentence);
//					processedContent.add(processedSentence);
//				}
//
//				scrappedData.setProcessedTitle(processedTitle);
//				scrappedData.setProcessedContent(processedContent);
//			}
//
//			return scrappedDataList;
//		}
//	}
	

	
	private List<ScrapedData> readAndPreprocessJsonFile(String jsonFilePath) throws IOException {
	    ClassPathResource resource = new ClassPathResource(jsonFilePath);

	    try (InputStream inputStream = resource.getInputStream()) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<ScrapedData> scrappedDataList = new ArrayList<>(); // Initialize the list

	        try {
	            scrappedDataList = objectMapper.readValue(inputStream, new TypeReference<List<ScrapedData>>() {});
	        } catch (IOException e) {
	            System.err.println("Error parsing JSON line: " + e.getMessage());
	            // You may choose to log the error or handle it differently based on your needs.
	        }

	        // Preprocess each object in the array and skip if any required field is null
	        List<ScrapedData> processedDataList = new ArrayList<>();
	        for (ScrapedData scrappedData : scrappedDataList) {
	            // Check if any required field is null
	            if (scrappedData.getTitle() == null || scrappedData.getContent() == null || scrappedData.getUrl() == null) {
	                System.err.println("Skipping ScrapedData due to null field(s).");
	                continue;
	            }

	            // Preprocess the data as before
	            String originalTitle = scrappedData.getTitle();
	            List<String> originalContent = scrappedData.getContent();
	            String processedTitle = applyStopWordsRemovalAndStemmingToText(originalTitle);
	            List<String> processedContent = new ArrayList<>();
	            for (String sentence : originalContent) {
	                String processedSentence = applyStopWordsRemovalAndStemmingToText(sentence);
	                processedContent.add(processedSentence);
	            }
	            scrappedData.setProcessedTitle(processedTitle);
	            scrappedData.setProcessedContent(processedContent);

	            // Add the processed ScrapedData to the list
	            processedDataList.add(scrappedData);
	        }

	        return processedDataList;
	    }
	}



	// document representation
	private List<Document> createDocuments(List<ScrapedData> scrappedDataList) {
		List<Document> documents = new ArrayList<>();

		try (IndexReader reader = DirectoryReader.open(index)) {
			IndexSearcher searcher = new IndexSearcher(reader);

			for (ScrapedData scrappedData : scrappedDataList) {
				String url = scrappedData.getUrl();

				// Check if a document with the same URL already exists in the index
				TermQuery query = new TermQuery(new Term("url", url));
				TopDocs topDocs = searcher.search(query, 1);
				if (topDocs.totalHits.value > 0) {
					// Document with the same URL already exists, skip adding it again
					System.out.println("[Preprocessing] Skipped adding document.");
					continue;
				}

				// Create a new document and add it to the list
				Document document = new Document();
				
				document.add(new StringField("url", scrappedData.getUrl(), Field.Store.YES));
				document.add(new StringField("title", scrappedData.getProcessedTitle(), Field.Store.YES));
				document.add(
						new TextField("content", String.join(" ", scrappedData.getProcessedContent()), Field.Store.YES));
				document.add(new StringField("dateTime", scrappedData.getDatetime(), Field.Store.YES));
				document.add(new StringField("originalTitle", scrappedData.getTitle(), Field.Store.YES));
				
				documents.add(document);
				System.out.println("[Preprocessing] Adding document.");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create documents", e);
		}
//		for (ScrapedData scrappedData : scrappedDataList) {
//			Document document = new Document();
//			document.add(new StringField("url", scrappedData.getUrl(), Field.Store.YES));
//			document.add(new StringField("title", scrappedData.getProcessedTitle(), Field.Store.YES));
//			document.add(
//					new TextField("content", String.join(" ", scrappedData.getProcessedContent()), Field.Store.YES));
//			document.add(new StringField("dateTime", scrappedData.getDatetime(), Field.Store.YES));
//			document.add(new StringField("originalTitle", scrappedData.getTitle(), Field.Store.YES));
//			documents.add(document);
//		}

		return documents;
	}

	private void indexDocuments(List<Document> documents) {
		try (IndexWriter writer = new IndexWriter(index, config)) {
			for (Document document : documents) {
				writer.addDocument(document);
			}
			writer.commit();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to index documents", e);
		}
	}

	private String applyStopWordsRemovalAndStemmingToText(String text) {
		
		// Check if the input text is null or empty
//	    if (text == null || text.isEmpty()) {
//	        return ""; // Omg....wth // use too much chatgpt and loss the ability to code...
//	    }
		
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
		// System.out.println(processedText.toString().trim());
		return processedText.toString().trim();
	}
}
