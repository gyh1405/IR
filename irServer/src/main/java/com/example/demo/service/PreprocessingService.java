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
            List<ScrapedData> scrappedDataList = readAndPreprocessJsonFile(jsonFilePath);
            indexDocuments(createDocuments(scrappedDataList));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to preprocess and index data", e);
        }
    }

    private List<ScrapedData> readAndPreprocessJsonFile(String jsonFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        
        
        
        try (InputStream inputStream = resource.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ScrapedData> scrappedDataList;
            try {
                scrappedDataList = objectMapper.readValue(inputStream, new TypeReference<List<ScrapedData>>() {});
            } catch (IOException e) {
                throw new IllegalStateException("Failed to parse JSON file", e);
            }

            // Preprocess each object in the array
            for (ScrapedData scrappedData : scrappedDataList) {
                String originalTitle = scrappedData.getTitle();
                List<String> originalContent = scrappedData.getContent();
                String processedTitle = applyStopWordsRemovalAndStemmingToText(originalTitle);
                List<String> processedContent = new ArrayList<>();
                for (String sentence : originalContent) {
                    String processedSentence = applyStopWordsRemovalAndStemmingToText(sentence);
                    processedContent.add(processedSentence);
                }
                scrappedData.setTitle(originalTitle);
                scrappedData.setContent(originalContent);
                scrappedData.setProcessedTitle(processedTitle);
                scrappedData.setProcessedContent(processedContent);
            }

            return scrappedDataList;
        }
    }

    private List<Document> createDocuments(List<ScrapedData> scrappedDataList) {
        List<Document> documents = new ArrayList<>();
        for (ScrapedData scrappedData : scrappedDataList) {
            Document document = new Document();
            document.add(new StringField("title", scrappedData.getProcessedTitle(), Field.Store.YES));
            document.add(new TextField("content", String.join(" ", scrappedData.getProcessedContent()), Field.Store.YES));
            documents.add(document);
        }
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
        StringBuilder processedText = new StringBuilder();
        try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
             StopFilter stopFilter = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet())) {
            CharTermAttribute charTermAttribute = stopFilter.addAttribute(CharTermAttribute.class);
            stopFilter.reset();
            while (stopFilter.incrementToken()) {
                String term = charTermAttribute.toString();
                processedText.append(term).append(" ");
            }
            stopFilter.end();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to apply stop words removal", e);
        }
        return processedText.toString().trim();
    }
}
