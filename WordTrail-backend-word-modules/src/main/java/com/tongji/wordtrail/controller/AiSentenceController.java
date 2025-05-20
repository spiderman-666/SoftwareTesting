package com.tongji.wordtrail.controller;

import com.tongji.wordtrail.service.AiSentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tongji.wordtrail.dto.StoryRequestDto;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiSentenceController {

    @Autowired
    private AiSentenceService aiSentenceService;

    @GetMapping("/generate-sentence")
    public String generateSentence(@RequestParam String language, @RequestParam String word) {
        return aiSentenceService.generateExampleSentence(language, word);
    }
    @PostMapping("/generate-story")
    public String generateStory(@RequestBody StoryRequestDto request) {
        if (request.getWords() == null || request.getWords().isEmpty()) {
            throw new IllegalArgumentException("Words list cannot be null or empty.");
        }
        return aiSentenceService.generateStory(request.getLanguage(), request.getWords());
    }
}