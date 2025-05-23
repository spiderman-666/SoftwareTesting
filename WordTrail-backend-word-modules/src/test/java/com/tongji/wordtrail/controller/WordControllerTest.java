package com.tongji.wordtrail.controller;

import com.tongji.wordtrail.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // ✅ 禁用 Spring Security 过滤器
public class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordService wordService;

    @Test
    public void testGetWordsWithIds() throws Exception {
        // 模拟返回
        List<Map<String, Object>> mockResults = new ArrayList<>();
        Map<String, Object> word1 = new HashMap<>();
        word1.put("id", "1");
        word1.put("word", "hello");
        mockResults.add(word1);

        when(wordService.getWordsByIds(Arrays.asList("1", "2"))).thenReturn(mockResults);

        mockMvc.perform(get("/api/v1/words?ids=1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].word").value("hello"));
    }

    @Test
    public void testGetWordsWithQueryParams() throws Exception {
        // 模拟返回
        List<Map<String, Object>> mockResults = new ArrayList<>();
        Map<String, Object> word = new HashMap<>();
        word.put("word", "world");
        mockResults.add(word);

        Map<String, String> params = new HashMap<>();
        params.put("difficulty", "easy");

        when(wordService.getWords(params)).thenReturn(mockResults);

        mockMvc.perform(get("/api/v1/words?difficulty=easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("world"));
    }
}
