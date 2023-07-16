package com.example.demo;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class IndexFactory {
    private static final ByteBuffersDirectory index = createIndex();

    public static ByteBuffersDirectory getIndex() {
        return index;
    }

    private static ByteBuffersDirectory createIndex() {
        ByteBuffersDirectory index = new ByteBuffersDirectory();

        // Check if the index already exists
        if (indexExists()) {
            return index;
        }

        try (IndexWriter writer = createIndexWriter(index)) {
            addJsonDocs(writer, "C:\\Users\\leeli\\eclipse-workspace\\demo-1\\src\\main\\resources\\scrapped_results.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return index;
    }

    private static boolean indexExists() {
        // Check if the index files exist
        // Return true if the index exists, false otherwise
        // Implement your logic here
        return false;
    }

    private static IndexWriter createIndexWriter(ByteBuffersDirectory index) throws IOException {
    	StandardAnalyzer analyzer = createAnalyzerWithStopwords();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        return new IndexWriter(index, config);
    }
    
    private static StandardAnalyzer createAnalyzerWithStopwords() {
    	List<String> stopWordsList = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
                "if", "in", "into", "is", "it", "no", "not", "of", "on",
                "or", "such", "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"
        );
        CharArraySet stopWords = new CharArraySet(stopWordsList, true);
        return new StandardAnalyzer(stopWords);
    }

    private static void addJsonDocs(IndexWriter w, String jsonFilePath) throws IOException {
    	JSONTokener tokener = new JSONTokener(new FileReader(jsonFilePath));

        // Use JSONTokener to create a JSONArray from the FileReader
        
        try (FileReader fileReader = new FileReader(jsonFilePath)) {
        	JSONArray jsonArray = new JSONArray(tokener);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Document doc = new Document();

                Object titleValue = jsonObject.get("title");
                if (titleValue != null) {
                    // Index the title field with stemming
                    StandardAnalyzer analyzer = createAnalyzerWithStopwords();
                    TokenStream titleTokenStream = analyzer.tokenStream("title", new StringReader(titleValue.toString()));
                    titleTokenStream = new PorterStemFilter(titleTokenStream);

                    doc.add(new TextField("title", titleValue.toString(), Field.Store.YES));
                }

                w.addDocument(doc);

                Object contentValue = jsonObject.get("content");
                if (contentValue != null) {
                	// Index the title field with stemming
                    StandardAnalyzer analyzer = createAnalyzerWithStopwords();
                    TokenStream contentTokenStream = analyzer.tokenStream("content", new StringReader(contentValue.toString()));
                    contentTokenStream = new PorterStemFilter(contentTokenStream);

                    doc.add(new TextField("content", contentValue.toString(), Field.Store.YES));
                }

                w.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}