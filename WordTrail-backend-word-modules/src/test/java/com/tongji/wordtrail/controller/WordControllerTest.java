package com.tongji.wordtrail.controller;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("单词控制器测试")
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 公共测试数据常量
    private static final ObjectId VALID_WORD_ID = new ObjectId("67eb986cc015ca11e33b4e86");
    private static final ObjectId NON_EXISTENT_WORD_ID = new ObjectId("507f1f77bcf86cd799439999");
    private static final String INVALID_WORD_ID = "abc";

    @Nested
    @DisplayName("获取单个单词的详细信息")
    class GetWordDetailTests {

        @Test
        @DisplayName("TC2-2-4-1: 正常请求 - 验证合法wordId是否能返回正确的单词信息")
        void TC2_2_4_1_正常请求() throws Exception {
            mockMvc.perform(get("/api/v1/words/" + VALID_WORD_ID.toHexString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(VALID_WORD_ID.toHexString()))
                    .andExpect(jsonPath("$.word").exists())
                    .andExpect(jsonPath("$.language").exists());
        }

        @Test
        @DisplayName("TC2-2-4-2: wordId非法格式 - 检查系统对非法格式是否能拒绝并提示")
        void TC2_2_4_2_wordId非法格式() throws Exception {
            // wordId 不是有效的 ObjectId 格式，将触发 Spring 参数绑定失败
            mockMvc.perform(get("/api/v1/words/" + INVALID_WORD_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-4-3: wordId不存在 - 验证对数据库中不存在的单词是否返回合适的错误")
        void TC2_2_4_3_wordId不存在() throws Exception {
            mockMvc.perform(get("/api/v1/words/" + NON_EXISTENT_WORD_ID.toHexString()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC2-2-4-4: 缺失wordId参数 - 检查系统对缺失路径参数是否会报错")
        void TC2_2_4_4_缺失wordId参数() throws Exception {
            // 请求路径缺失wordId参数，会导致路由不匹配
            mockMvc.perform(get("/api/v1/words/"))
                    .andExpect(status().isNotFound()); // 路由不匹配返回404
        }
    }
}
