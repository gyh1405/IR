package lucene.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
            addJsonDocs(writer, "C:\\Users\\leeli\\eclipse-workspace\\Lucene\\src\\lucene\\test\\data\\scrapped_results.json");
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
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(jsonFilePath)) {
            JSONArray jsonArray = (JSONArray) parser.parse(fileReader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;

                Document doc = new Document();

                Object titleValue = jsonObject.get("title");
                if (titleValue != null) {
                    doc.add(new TextField("title", titleValue.toString(), Field.Store.YES));
                }

                Object contentValue = jsonObject.get("content");
                if (contentValue != null) {
                    doc.add(new TextField("content", contentValue.toString(), Field.Store.YES));
                }

                w.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
