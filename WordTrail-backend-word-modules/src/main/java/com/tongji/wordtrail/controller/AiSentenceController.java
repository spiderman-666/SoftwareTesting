package com.tongji.wordtrail.controller;

import com.tongji.wordtrail.service.AiSentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> generateStory(@RequestBody StoryRequestDto request) {
        if (request.getWords() == null || request.getWords().isEmpty()) {
            return ResponseEntity.badRequest().body("Words list cannot be null or empty.");
        }
        if (request.getWords().size() > 10) {
            return ResponseEntity.badRequest().body("Words list cannot contain more than 10 words.");
        }
        try {
            String result = aiSentenceService.generateStory(request.getLanguage(), request.getWords());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("生成失败，请稍后重试");
        }
    }
}