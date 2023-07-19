package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.service.PreprocessingService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.demo")
public class DemoApplication {

    private final PreprocessingService preprocessingService;

    public DemoApplication(PreprocessingService preprocessingService) {
        this.preprocessingService = preprocessingService;
    }

    public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @PostConstruct
    public void preprocessData() {
    	
    	ClassPathResource scrapedResults = new ClassPathResource("scraped_data/scrapped_results.json");
    	ClassPathResource visitedURLs = new ClassPathResource("visited_urls/visited_urls.txt");
        
        
        String jsonFilePath = scrapedResults.getPath();
        String txtFilePath = visitedURLs.getPath();
        preprocessingService.preprocessAndIndexDataFromFiles(jsonFilePath, txtFilePath);
    }
}

//    /demo/src/main/resources/scraped_data/scrapped_results.json
//    /demo/src/main/resources/visited_urls/visited_urls.json
