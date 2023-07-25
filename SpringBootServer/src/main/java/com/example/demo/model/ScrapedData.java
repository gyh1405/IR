package com.example.demo.model;

import java.util.List;

public class ScrapedData {
	private String url;
	private String dateTime;
    private String title;
    private List<String> content;
    private String processedTitle;
    private List<String> processedContent;

    public ScrapedData() {
    }

    public ScrapedData(String title, List<String> content) {
        this.title = title;
        this.content = content;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getDatetime() {
        return dateTime;
    }
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getProcessedTitle() {
        return processedTitle;
    }

    public void setProcessedTitle(String processedTitle) {
        this.processedTitle = processedTitle;
    }

    public List<String> getProcessedContent() {
        return processedContent;
    }

    public void setProcessedContent(List<String> processedContent) {
        this.processedContent = processedContent;
    }
    
    // Other methods, constructors, getters, setters, etc.
}
