package com.example.demo.service;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LuceneSearchService {

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
			addJsonDocs(writer,
					"/Users/gyh/eclipse-workspace/demo/src/main/resources/scraped_data/scrapped_results.json");
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
		List<String> stopWordsList = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if",
				"in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then",
				"there", "these", "they", "this", "to", "was", "will", "with");
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
					TokenStream titleTokenStream = analyzer.tokenStream("title",
							new StringReader(titleValue.toString()));
					titleTokenStream = new PorterStemFilter(titleTokenStream);

					doc.add(new TextField("title", titleValue.toString(), Field.Store.YES));
				}

				w.addDocument(doc);

				Object contentValue = jsonObject.get("content");
				if (contentValue != null) {
					// Index the title field with stemming
					StandardAnalyzer analyzer = createAnalyzerWithStopwords();
					TokenStream contentTokenStream = analyzer.tokenStream("content",
							new StringReader(contentValue.toString()));
					contentTokenStream = new PorterStemFilter(contentTokenStream);

					doc.add(new TextField("content", contentValue.toString(), Field.Store.YES));
				}

				w.addDocument(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static Query createQuery(String querystr, StandardAnalyzer analyzer) throws ParseException {
//		String[] fields = { "title", "content" };
//		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
//		Query query = parser.parse(querystr);
//
//		// Boost the "content" field to prioritize it based on term frequency
//		TermQuery contentQuery = new TermQuery(new Term("content", querystr));
//		BoostQuery boostedContentQuery = new BoostQuery(contentQuery, 2.0f); // Increase the boost factor as desired
//
//		// Create a BooleanQuery to combine the original query and the boosted content
//		// query
//		BooleanQuery.Builder builder = new BooleanQuery.Builder();
//		builder.add(query, BooleanClause.Occur.SHOULD);
//		builder.add(boostedContentQuery, BooleanClause.Occur.SHOULD);
//		return builder.build();
//	}

	public static Query createQuery(String querystr, StandardAnalyzer analyzer) throws ParseException {
		String[] fields = { "title", "content" };
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
		Query query = parser.parse(querystr);

		// Boost the "content" field to prioritize it based on term frequency
		TermQuery contentQuery = new TermQuery(new Term("content", querystr));
		BoostQuery boostedContentQuery = new BoostQuery(contentQuery, 2.0f); // Increase the boost factor as desired

		// Create a BooleanQuery to combine the original query and the boosted content
		// query
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(query, BooleanClause.Occur.SHOULD);
		builder.add(boostedContentQuery, BooleanClause.Occur.SHOULD);
		return builder.build();
	}

}
