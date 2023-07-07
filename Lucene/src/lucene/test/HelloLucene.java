package lucene.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HelloLucene {
    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        ByteBuffersDirectory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addJsonDocs(w, "C:\\Users\\leeli\\eclipse-workspace\\Lucene\\src\\lucene\\test\\data\\top_movies.json");
        w.close();

     // 2. query
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("Enter your query (or enter 'exit' to quit): ");
            String querystr = scanner.nextLine();

            if (querystr.equalsIgnoreCase("exit")) {
                break;
            }

            String[] fields = {"title", "rank", "rating", "year"};
            Query q = new MultiFieldQueryParser(fields, analyzer).parse(querystr);

            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". Ranking:" + d.get("rank") + " " + d.get("title") + " Rating:" + d.get("rating") + " Year:" + d.get("year"));
            }

            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close();
            System.out.println("\n");
        }
        
        scanner.close();
    }

    private static void addJsonDocs(IndexWriter w, String jsonFilePath) throws IOException {
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(jsonFilePath)) {
            JSONArray jsonArray = (JSONArray) parser.parse(fileReader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Document doc = new Document();
                doc.add(new TextField("title", jsonObject.get("title").toString(), Field.Store.YES));
                doc.add(new TextField("rank", jsonObject.get("rank").toString(), Field.Store.YES));
                doc.add(new TextField("rating", jsonObject.get("rating").toString(), Field.Store.YES));
                doc.add(new TextField("year", jsonObject.get("year").toString(), Field.Store.YES));
                w.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}