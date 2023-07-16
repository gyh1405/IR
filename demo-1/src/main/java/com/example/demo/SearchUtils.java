package com.example.demo;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class SearchUtils {
    public static Query createQuery(String querystr, StandardAnalyzer analyzer) throws ParseException {
        String[] fields = {"title", "content"};
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        Query query = parser.parse(querystr);
        
        // Boost the "content" field to prioritize it based on term frequency
        TermQuery contentQuery = new TermQuery(new Term("content", querystr));
        BoostQuery boostedContentQuery = new BoostQuery(contentQuery, 2.0f); // Increase the boost factor as desired
        
        // Create a BooleanQuery to combine the original query and the boosted content query
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(query, BooleanClause.Occur.SHOULD);
        builder.add(boostedContentQuery, BooleanClause.Occur.SHOULD);
        return builder.build();
    }
}
