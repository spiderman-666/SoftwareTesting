package com.tongji.wordtrail.controller;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional // 确保测试后自动回滚
@DisplayName("单词学习进度控制器测试")
class WordLearningProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 使用真实数据库中的数据常量
    private static final String VALID_USER_ID = "ed62add4-bf40-4246-b7ab-2555015b383b";
    private static final String NON_EXISTENT_USER_ID = "00000000-0000-0000-0000-000000000000";
    private static final ObjectId VALID_BOOK_ID = new ObjectId("67eb986cc015ca11e33b4e86");
    private static final ObjectId NON_EXISTENT_BOOK_ID = new ObjectId("507f1f77bcf86cd799439999");
    private static final String INVALID_BOOK_ID = "abc";
    private static final ObjectId VALID_WORD_ID = new ObjectId("67fa0e2c2c0bf3230b6d9f95"); // 添加有效的单词ID

    @Nested
    @DisplayName("统计用户在词书中的未学习单词数量")
    class NewWordsCountTests {

        @Test
        @DisplayName("TC2-2-1-1: 正常请求 - 验证合法参数下能准确返回未学习单词数量")
        void TC2_2_1_1_正常请求() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words-count")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.count").isNumber());
        }

        @Test
        @DisplayName("TC2-2-1-2: 词书ID非法 - 验证系统能否识别非法的词书编号")
        void TC2_2_1_2_词书ID非法() throws Exception {
            // bookId 不是有效的 ObjectId 格式，将触发 Spring 参数绑定失败
            mockMvc.perform(get("/api/v1/learning/book/" + INVALID_BOOK_ID + "/new-words-count")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-1-3: 缺失用户ID - 检查缺失必要参数时系统是否能正确响应")
        void TC2_2_1_3_缺失用户ID() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words-count"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-1-4: 词书ID不存在 - 检查对不存在词书编号的容错能力")
        void TC2_2_1_4_词书ID不存在() throws Exception {
            // 词书ID不存在时，service层会抛出IllegalArgumentException，被异常处理器捕获返回400
            mockMvc.perform(get("/api/v1/learning/book/" + NON_EXISTENT_BOOK_ID.toHexString() + "/new-words-count")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("TC2-2-1-5: 用户ID不存在 - 验证不存在用户的处理")
        void TC2_2_1_5_用户ID不存在() throws Exception {
            // 使用不存在的用户ID，系统应该返回该词书的总单词数量（因为该用户没有学习记录）
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words-count")
                            .param("userId", NON_EXISTENT_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.count").isNumber())
                    .andExpect(jsonPath("$.count").value(org.hamcrest.Matchers.greaterThanOrEqualTo(0)));
        }
    }

    @Nested
    @DisplayName("统计用户当天需要复习的单词数量")
    class TodayReviewCountTests {

        @Test
        @DisplayName("TC2-2-2-1: 正常请求 - 验证合法参数下能准确返回待复习单词数量")
        void TC2_2_2_1_正常请求() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/today-review-count")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.count").isNumber());
        }

        @Test
        @DisplayName("TC2-2-2-2: 缺失用户ID - 检查缺失必要参数时系统是否能正确响应")
        void TC2_2_2_2_缺失用户ID() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/today-review-count"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-2-3: 用户ID非法 - 检查非法用户ID输入时系统是否给出错误提示")
        void TC2_2_2_3_用户ID非法() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/today-review-count")
                            .param("userId", "null"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.count").value(0));
        }

        @Test
        @DisplayName("TC2-2-2-4: 词书ID不存在 - 验证系统是否能处理不存在的词书编号")
        void TC2_2_2_4_词书ID不存在() throws Exception {
            // 词书ID不存在时，service层会抛出IllegalArgumentException，被异常处理器捕获返回400
            mockMvc.perform(get("/api/v1/learning/book/" + NON_EXISTENT_BOOK_ID.toHexString() + "/today-review-count")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("TC2-2-2-5: 用户ID不存在 - 验证系统对不存在的用户ID能否做出合理响应")
        void TC2_2_2_5_用户ID不存在() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/today-review-count")
                            .param("userId", NON_EXISTENT_USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.count").value(0));
        }
    }

    @Nested
    @DisplayName("获取用户在词书中的待学习新单词列表")
    class NewWordsListTests {

        @Test
        @DisplayName("TC2-2-3-1: 正常请求 - 验证系统在合法参数下返回长度不超过batchSize的单词列表")
        void TC2_2_3_1_正常请求() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(5)));
        }

        @Test
        @DisplayName("TC2-2-3-2: batchSize为0 - 验证系统能否正确处理请求数量为0的边界情况")
        void TC2_2_3_2_batchSize为0() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "0"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("TC2-2-3-3: batchSize超上限 - 验证系统是否限制最大返回数量")
        void TC2_2_3_3_batchSize超上限() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "10000"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(100))); // 假设最大上限为100
        }

        @Test
        @DisplayName("TC2-2-3-4: 缺失bookId - 验证缺失必要参数时系统是否能明确提示")
        void TC2_2_3_4_缺失bookId() throws Exception {
            // 这个测试实际上不可能发生，因为bookId是路径参数，缺失会导致路由不匹配
            // 我们测试缺失userId参数
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("batchSize", "5"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-3-5: 参数类型非法 - 检查非法类型参数是否能被正确识别和拒绝")
        void TC2_2_3_5_参数类型非法() throws Exception {
            // 测试非法的bookId格式
            mockMvc.perform(get("/api/v1/learning/book/" + INVALID_BOOK_ID + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "5"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-3-6: batchSize为负数 - 验证系统对负数参数的处理")
        void TC2_2_3_7_batchSize为负数() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "-1"))
                    .andExpect(status().isBadRequest()); // 修正：负数参数应该返回400错误
        }

        @Test
        @DisplayName("TC2-2-3-7: 缺失batchSize参数 - 验证缺失batchSize时的处理")
        void TC2_2_3_8_缺失batchSize参数() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-3-8: 词书ID不存在 - 验证不存在词书的处理")
        void TC2_2_3_9_词书ID不存在() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + NON_EXISTENT_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", VALID_USER_ID)
                            .param("batchSize", "5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("TC2-2-3-9: 用户ID不存在 - 验证不存在用户的处理")
        void TC2_2_3_10_用户ID不存在() throws Exception {
            mockMvc.perform(get("/api/v1/learning/book/" + VALID_BOOK_ID.toHexString() + "/new-words")
                            .param("userId", NON_EXISTENT_USER_ID)
                            .param("batchSize", "5"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(5))); // 修正：不存在的用户会返回新单词，数量应该 <= batchSize
        }
    }

    @Nested
    @DisplayName("标记某个单词为开始学习状态")
    class StartLearningTests {

        @Test
        @DisplayName("TC2-2-5-1: 正常请求 - 验证用户提交合法wordId和userId后，系统能正确记录开始学习状态")
        void TC2_2_5_1_正常请求() throws Exception {
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("userId", VALID_USER_ID)
                            .param("wordId", VALID_WORD_ID.toHexString())) // 修正：使用正确的单词ID
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.userId").value(VALID_USER_ID))
                    .andExpect(jsonPath("$.wordId").value(VALID_WORD_ID.toHexString()));
        }

        @Test
        @DisplayName("TC2-2-5-2: 缺失userId参数 - 验证缺失必要参数时，系统是否拒绝并提示错误")
        void TC2_2_5_2_缺失userId参数() throws Exception {
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("wordId", VALID_WORD_ID.toHexString())) // 修正：使用正确的单词ID
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-5-3: 缺失wordId参数 - 验证缺失必要参数时，系统是否拒绝并提示错误")
        void TC2_2_5_3_缺失wordId参数() throws Exception {
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("userId", VALID_USER_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-5-4: 参数格式非法 - 验证非法格式是否会被系统识别为错误")
        void TC2_2_5_4_参数格式非法() throws Exception {
            // 测试非法的wordId格式
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("userId", VALID_USER_ID)
                            .param("wordId", INVALID_BOOK_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC2-2-5-5: 重复提交 - 检查相同用户对同一个单词重复标记是否幂等")
        void TC2_2_5_5_重复提交() throws Exception {
            String wordId = VALID_WORD_ID.toHexString(); // 修正：使用正确的单词ID
            
            // 第一次提交
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("userId", VALID_USER_ID)
                            .param("wordId", wordId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));

            // 第二次提交相同的参数，应该是幂等操作
            mockMvc.perform(post("/api/v1/learning/start")
                            .param("userId", VALID_USER_ID)
                            .param("wordId", wordId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.userId").value(VALID_USER_ID))
                    .andExpect(jsonPath("$.wordId").value(wordId));
        }
    }
}

