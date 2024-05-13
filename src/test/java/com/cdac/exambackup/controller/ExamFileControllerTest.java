package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamFileReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExamFileControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    Path path = Path.of("application.yml");


    @Test
    @Order(1)
    void shouldReturnData_forValidId() throws Exception {
        mockMvc.perform(get("/exam-files/{id}", 1)).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void shouldFail_forInvalidId() throws Exception {
        mockMvc.perform(get("/exam-files/{id}", 7)).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void shouldCreate_forValidData() throws Exception {
        MockMultipartFile file = new MockMultipartFile("build.gradle", "build.gradle", "text/plain", Files.readAllBytes(path));
        var examFileReqDto = new ExamFileReqDto(file, 1L, 1L, 1L, 1L, LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/exam-files/create")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(examFileReqDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void shouldFail_forInvalidData() throws Exception {
        MockMultipartFile file = new MockMultipartFile("empty.txt", "empty.txt", "text/plain", new byte[]{});
        var examFileReqDto = new ExamFileReqDto(file, 1L, 1L, 1L, 1L, LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/exam-files/create")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(examFileReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void shouldFail_forValidDataAndInvalidId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("build.gradle", "build.gradle", "text/plain", Files.readAllBytes(path));
        var examFileReqDto = new ExamFileReqDto(file, 7L, 1L, 1L, 1L, LocalDateTime.now());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/exam-files/create")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(objectMapper.writeValueAsString(examFileReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}