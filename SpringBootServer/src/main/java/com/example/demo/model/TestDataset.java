package com.example.demo.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataset {

    public static Map<String, List<String>> createTestDataset() {
        Map<String, List<String>> testDataset = new HashMap<>();

        // Define your queries and relevant documents here
        String query1 = "glasgow";
        
        List<String> relevantDocsQuery1 = Arrays.asList(
        		"https://www.encyclopedia.com/women/encyclopedias-almanacs-transcripts-and-maps/glasgow-ellen-1873-1945",
        		"https://www.encyclopedia.com/humanities/encyclopedias-almanacs-transcripts-and-maps/glaser-donald-arthur",
        		"https://www.encyclopedia.com/women/encyclopedias-almanacs-transcripts-and-maps/glaspell-susan-1876-1948"
        		);
        
        testDataset.put(query1, relevantDocsQuery1);

        return testDataset;
    }
}


//String query2 = "search query 2";

//List<String> relevantDocsQuery2 = Arrays.asList(
//		UUID.randomUUID(), 
//		UUID.randomUUID(), ...
//		); 
//
//testDataset.put(query2, relevantDocsQuery2);
