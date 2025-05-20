package com.tongji.wordtrail.controller;

import com.tongji.wordtrail.dto.AdminWordResponse;
import com.tongji.wordtrail.dto.AdminWordbookRequest;
import com.tongji.wordtrail.dto.AdminWordbooksResponse;
import com.tongji.wordtrail.dto.AuthResponse;
import com.tongji.wordtrail.model.Words;
import com.tongji.wordtrail.service.AdminWordbookService;
import com.tongji.wordtrail.service.SystemWordbookService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wordbooks")
public class AdminWordbookController {
    private static final Logger logger = LoggerFactory.getLogger(AdminWordbookController.class);
    private final AdminWordbookService adminWordbookService;
    private final SystemWordbookService systemWordbookService;
    public AdminWordbookController(AdminWordbookService adminWordbookService, SystemWordbookService systemWordbookService) {
        this.adminWordbookService = adminWordbookService;
        this.systemWordbookService = systemWordbookService;
        logger.info("AdminWordbookController initialized");
    }
    // 获取词汇列表
    @GetMapping("")
    public ResponseEntity<List<AdminWordbooksResponse>> getWordbooks() {
        try {
            // 获取所有词书的详细信息
            List<Map<String, Object>> wordbookDetails = adminWordbookService.findWordbooks();

            // 将 Map 转换为 AdminWordbooksResponse 对象
            List<AdminWordbooksResponse> response = wordbookDetails.stream().map(wordbook -> {
                AdminWordbooksResponse adminResponse = new AdminWordbooksResponse();
                adminResponse.setId((String) wordbook.get("id"));
                adminResponse.setBookName((String) wordbook.get("bookName"));
                adminResponse.setLanguage((String) wordbook.get("language"));
                adminResponse.setDescription((String) wordbook.get("description"));
                adminResponse.setWordCount((Integer) wordbook.get("wordCount"));
                return adminResponse;
            }).collect(Collectors.toList());

            logger.info("Find wordbooks successfully, found {} wordbooks", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Finding wordbooks failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // 获取词汇详情
    @GetMapping("/{wordbookId}")
    public ResponseEntity<Map<String, Object>> getWords(@PathVariable String wordbookId) {
        Optional<Map<String, Object>> result = systemWordbookService.getSystemWordbook(wordbookId);
        if (result.isPresent()) {
            // 获取 words 列表
            Map<String, Object> wordbookData = result.get();
            ObjectId id = new ObjectId(wordbookId);
            List<Words> words = adminWordbookService.findWords(id);
            for (Words word : words) {
                Object wordId = word.getId();

            }
            wordbookData.remove("words");
            wordbookData.put("words", words);
            return ResponseEntity.ok(result.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Wordbook not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    // 添加词书
    @PostMapping("/addWordbook")
    public ResponseEntity<?> addWordbooks(@RequestBody Map<String, Object> wordbookData) {
        try {
            logger.info("Add wordbooks...");
            Map<String, Object> result = adminWordbookService.createSystemWordbook(wordbookData);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Wordbook not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch(Exception e) {
            logger.error("Adding wordbooks failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // 向词书中添加新词汇
    @PostMapping("/{workbookId}/vocabularies")
    public ResponseEntity<?> addWordAndAddToWordbook(@PathVariable String workbookId, @RequestBody Words word) {
        try {
            // Step 1: 保存新词汇
            Words savedWord = adminWordbookService.createWord(word);
            String wordId = savedWord.getId().toHexString();

            logger.info("New word saved with ID: {}", wordId);

            // Step 2: 将新词汇加入词书
            Optional<?> result = systemWordbookService.addWordsToWordbook(workbookId, Collections.singletonList(wordId));

            if (result.isPresent()) {
                logger.info("Successfully added word to wordbook {}", workbookId);
                return ResponseEntity.ok(result.get());
            } else {
                logger.warn("Wordbook not found with ID: {}", workbookId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error saving word and adding to wordbook. Wordbook ID: {}", workbookId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save word or add to wordbook");
        }
    }

    // 删除新词汇
    @DeleteMapping("/{wordbookId}/vocabularies")
    public ResponseEntity<?> removeWordsFromWordbook(
            @PathVariable String wordbookId,
            @RequestBody Map<String, List<String>> requestBody) {
        try {
            List<String> wordIdStrings = requestBody.get("wordIds");
            if (wordIdStrings == null || wordIdStrings.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "wordIds cannot be empty"));
            }

            List<ObjectId> wordIds = wordIdStrings.stream()
                    .map(ObjectId::new)
                    .collect(Collectors.toList());

            Optional<Map<String, Object>> updatedWordbook = systemWordbookService.removeWordsFromWordbook(wordbookId, wordIds);

            return updatedWordbook
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to remove words from wordbook");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    // 删除词书
    @DeleteMapping("/{wordbookId}")
    public ResponseEntity<?> removeWordbook(@PathVariable String wordbookId) {
        systemWordbookService.deleteSystemWordbook(wordbookId);
        return ResponseEntity.ok().body("{}");
    }

}
