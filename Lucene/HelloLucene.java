package lucene.test;

import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.queryparser.classic.ParseException;

public class HelloLucene {
    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. Create or open the index
        ByteBuffersDirectory index = IndexFactory.getIndex();

        // 2. Create the index reader
        IndexReader reader = DirectoryReader.open(index);

        // 3. Create the index searcher
        IndexSearcher searcher = new IndexSearcher(reader);

        // 4. Query and search
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.print("Enter your query (or enter 'exit' to quit): ");
            String querystr = scanner.nextLine();

            if (querystr.equalsIgnoreCase("exit")) {
                exit = true;
            } else {
                Query q = SearchUtils.createQuery(querystr, analyzer);

                if (q != null) {
                    TopDocs docs = searcher.search(q, 50);
                    ScoreDoc[] hits = docs.scoreDocs;

                    // 5. Display results
                    System.out.println("Found " + hits.length + " hits.");
                    for (int i = 0; i < hits.length; ++i) {
                        int docId = hits[i].doc;
                        Document d = searcher.doc(docId);
                        float termScore = hits[i].score; // Get the term score
                        System.out.println((i + 1) + ". Title: " + d.get("title") + " \nScore: " + termScore);
                    }
                }

                System.out.println("\n");
            }
        }

        // 6. Close the resources
        reader.close();
        scanner.close();
    }
}
