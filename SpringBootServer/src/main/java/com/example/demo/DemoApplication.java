package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.service.PreprocessingService;
import com.example.demo.service.LuceneSearchService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo")
public class DemoApplication {

	private final LuceneSearchService luceneSearchService;
    private final PreprocessingService preprocessingService;

    public DemoApplication(PreprocessingService preprocessingService, LuceneSearchService luceneSearchService) {
        this.preprocessingService = preprocessingService;
        this.luceneSearchService = luceneSearchService;
    }

    public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @PostConstruct
    public void preprocessData() {
    	

    	ClassPathResource scrapedResults = new ClassPathResource("scraped_data/scrapped_results2.json");
    	ClassPathResource visitedURLs = new ClassPathResource("visited_urls/url_frontier.txt");

        
        
        String jsonFilePath = scrapedResults.getPath();
        String txtFilePath = visitedURLs.getPath();
        preprocessingService.preprocessAndIndexDataFromFiles(jsonFilePath, txtFilePath);
        
        luceneSearchService.evaluate();
    }
}

//    /demo/src/main/resources/scraped_data/scrapped_results.json
//    /demo/src/main/resources/visited_urls/visited_urls.json
