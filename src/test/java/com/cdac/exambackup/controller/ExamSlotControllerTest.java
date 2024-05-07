package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.ExamSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExamSlotControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    void shouldReturnData_forValidId() throws Exception {
        mockMvc.perform(get("/exam-slots/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldFail_forInvalidId() throws Exception {
        mockMvc.perform(get("/exam-slots/{id}", 7))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void shouldCreate_forValidData() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(5, "new")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void shouldNotCreate_forInvalidData() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(0, " ")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void shouldNotCreate_forValidCodeAndInvalidName() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(5, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void shouldNotCreate_forValidNameAndInvalidCode() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(null, "valid name")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void shouldNotCreate_forAlreadyExistedCode() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(1, "new exam-slot name")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void shouldNotCreate_forAlreadyExistedName() throws Exception {
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(new ExamSlot(8, "SLOT_1")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void shouldUpdate_forValidDataAndValidId() throws Exception {
        var examSlot = new ExamSlot(1, "both fields valid");
        examSlot.setId(1L);
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(examSlot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    void shouldUpdate_forValidNameAndValidId() throws Exception {
        var examSlot = new ExamSlot(null, "only name valid");
        examSlot.setId(1L);
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(examSlot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    void shouldNotUpdate_forValidDataAndInvalidId() throws Exception {
        var examSlot = new ExamSlot(1, "incorrect id");
        examSlot.setId(6L);
        mockMvc.perform(post("/exam-slots/create")
                        .content(objectMapper.writeValueAsString(examSlot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}