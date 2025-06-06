package com.tongji.wordtrail.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DisplayName("AI 句子控制器测试")
class AiSentenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("生成包含若干学习单词的 AI 总结故事")
    class GenerateStoryTests {

        @Test
        @DisplayName("TC2-2-6-1: 正常请求 - 验证提交 10 个合法单词时是否能生成完整故事文本")
        void TC2_2_6_1_正常请求() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": [\"apple\", \"tree\", \"book\", \"water\", \"house\", \"car\", \"dog\", \"cat\", \"sun\", \"moon\"]\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("|")));
        }

        @Test
        @DisplayName("TC2-2-6-2: 单词数量不足 - 检查系统是否对不足 10 个单词的请求进行正常处理")
        void TC2_2_6_2_单词数量不足() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": [\"apple\", \"tree\"]\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("|")));
        }

        @Test
        @DisplayName("TC2-2-6-3: 单词列表为空 - 检查系统能否检测空列表并返回错误")
        void TC2_2_6_3_单词列表为空() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": []\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-6-4: 缺失语言参数 - 检查语言类型为可选参数或必填参数的行为")
        void TC2_2_6_4_缺失语言参数() throws Exception {
            String requestJson = "{\n" +
                "    \"words\": [\"apple\", \"banana\", \"orange\"]\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC2-2-6-5: 单词含特殊字符 - 检查包含特殊字符的词汇是否能被正常处理")
        void TC2_2_6_5_单词含特殊字符() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": [\"good!\", \"world@\", \"hello#world\"]\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC2-2-6-6: 请求体格式错误 - 提交非法结构是否会返回明确的错误提示")
        void TC2_2_6_6_请求体格式错误() throws Exception {
            String requestJson = "{\n" +
                "    \"word_list\": \"hello\"\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-6-7: 单词数量超限 - 检查超过 10 个单词时系统是否返回错误")
        void TC2_2_6_7_单词数量超限() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": [\"apple\", \"tree\", \"book\", \"water\", \"house\", \"car\", \"dog\", \"cat\", \"sun\", \"moon\", \"star\"]\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-6-8: null单词列表 - 检查单词列表为null时的处理")
        void TC2_2_6_8_null单词列表() throws Exception {
            String requestJson = "{\n" +
                "    \"language\": \"English\",\n" +
                "    \"words\": null\n" +
                "}";

            mockMvc.perform(post("/api/v1/ai/generate-story")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
        }
    }
}
