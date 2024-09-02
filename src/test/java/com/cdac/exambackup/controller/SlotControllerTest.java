package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.Slot;
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

import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SlotControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    String testJwtToken = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiaXNzIjoiY2RhYy5jb20iLCJpYXQiOjE3MjUzMDE1NzgsImV4cCI6MTc1NjQwNTU3OCwicGVybWlzc2lvbnMiOlsiNCJdLCJpZCI6MSwibmFtZSI6bnVsbCwia2V5IjpudWxsfQ.en3-Ubw7T1ciFNOiZ74CcNj6jDPlxwGaFQA4qHpS4bAY0j5ks0QEiirMPrABzBAHB1X6uyLxMAEQo0DC3Rey6g";


    @Test
    @Order(1)
    void shouldReturnData_forValidId() throws Exception {
        mockMvc.perform(get("/slots/{id}", 1).header("Authorization", testJwtToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldFail_forInvalidId() throws Exception {
        mockMvc.perform(get("/slots/{id}", 7).header("Authorization", testJwtToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void shouldCreate_forValidData() throws Exception {
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(new Slot("5", "Test 5", LocalTime.of(13, 0, 0), LocalTime.of(14, 0, 0))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void shouldNotCreate_forDuplicateCode() throws Exception {
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(new Slot("5", "Test 6", LocalTime.of(13, 0, 0), LocalTime.of(14, 0, 0))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void shouldNotCreate_forDuplicateName() throws Exception {
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(new Slot("6", "Test 5", LocalTime.of(13, 0, 0), LocalTime.of(14, 0, 0))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void shouldNotCreate_forDuplicateStartAndEndTime() throws Exception {
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(new Slot("7", "Test 7", LocalTime.of(13, 0, 0), LocalTime.of(14, 0, 0))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void shouldNotCreate_forStartTimeGreaterThanEndTime() throws Exception {
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(new Slot("7", "Test 7", LocalTime.of(15, 0, 0), LocalTime.of(14, 0, 0))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Order(8)
    void shouldUpdate_forValidDataAndValidId() throws Exception {
        var slot = new Slot("5", "Test 5 Updated", LocalTime.of(13, 0, 0), LocalTime.of(14, 0, 0));
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    void shouldUpdate_onlyCode() throws Exception {
        var slot = new Slot("10", null, null, null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    void shouldUpdate_onlyName() throws Exception {
        var slot = new Slot(null, "Test 5 Updated Name", null, null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    void shouldUpdate_onlyStartTime() throws Exception {
        var slot = new Slot(null, null, LocalTime.of(13, 30, 0), null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(12)
    void shouldUpdate_onlyEndTime() throws Exception {
        var slot = new Slot(null, null, null, LocalTime.of(14, 30, 0));
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(13)
    void shouldUpdate_bothStartTimeAndEndTime() throws Exception {
        var slot = new Slot(null, null, LocalTime.of(13, 30, 0), LocalTime.of(14, 30, 0));
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @Order(14)
    void shouldNotUpdate_forDuplicateCode() throws Exception {
        var slot = new Slot("1", null, null, null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    void shouldNotUpdate_forDuplicateName() throws Exception {
        var slot = new Slot(null, "Slot 1", null, null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(16)
    void shouldNotUpdate_forDuplicateStartAndEndTime() throws Exception {
        var slot = new Slot(null, null, LocalTime.of(9, 0, 0), LocalTime.of(10, 0, 0));
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(17)
    void shouldNotUpdate_forInvalidData() throws Exception {
        var slot = new Slot(null, null, null, null);
        slot.setId(5L);
        mockMvc.perform(post("/slots/create")
                        .header("Authorization", testJwtToken)
                        .content(objectMapper.writeValueAsString(slot))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}