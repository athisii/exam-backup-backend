package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.AppUser;
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
class AppAppUserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    void shouldReturnData_forValidId() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldFail_forInvalidId() throws Exception {
        mockMvc.perform(get("/users/{id}", 7))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void shouldUpdate_forValidDataAndValidId() throws Exception {
        var user = new AppUser();
        user.setMobileNumber("8132817645");
        user.setEmail("email@email.com");
        user.setId(1L);
        mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void shouldUpdate_forValidEmailAndValidId() throws Exception {
        var user = new AppUser();
        user.setEmail("email@email.com");
        user.setId(1L);
        mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void shouldUpdate_forValidMobileNumberAndValidId() throws Exception {
        var user = new AppUser();
        user.setMobileNumber("+91-8132817645");
        user.setId(1L);
        mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void shouldNotUpdate_forInvalidDataAndValidId() throws Exception {
        var user = new AppUser();
        user.setMobileNumber("123");
        user.setEmail("3asdf@one");
        user.setId(2L);
        mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void shouldNotUpdate_forValidDataAndInvalidId() throws Exception {
        var user = new AppUser();
        user.setMobileNumber("+91 8132817645");
        user.setEmail("email@email.com");
        user.setId(9L);
        mockMvc.perform(post("/users/create")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}