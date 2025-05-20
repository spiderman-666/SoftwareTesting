package com.tongji.wordtrail.dto;

import java.util.List;

public class StoryRequestDto {
    private String language;
    private List<String> words;

    // Getters and Setters
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}