package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
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
class ExamCentreControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @Order(1)
    void shouldReturnData_forValidId() throws Exception {
        mockMvc.perform(get("/exam-centres/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void shouldFail_forInvalidId() throws Exception {
        mockMvc.perform(get("/exam-centres/{id}", 7))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void shouldCreate_forValidData() throws Exception {
        var region = new Region();
        region.setId(1L);
        var user = new AppUser();
        user.setEmail("athisii@email.com");
        user.setMobileNumber("8132817610");
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre("7", "EC7", region, null, user)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void shouldNotCreate_forInvalidData() throws Exception {
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre(" ", " ", null, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void shouldNotCreate_forValidCodeAndInvalidName() throws Exception {
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre("10", null, null, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void shouldNotCreate_forValidCodeAndValidNameAnsInvalidRegionId() throws Exception {
        var region = new Region();
        region.setId(20L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre("10", "valid name", region, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void shouldNotCreate_forValidNameAndInvalidCode() throws Exception {
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre(null, "valid name", null, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    void shouldNotCreate_forAlreadyExistedCode() throws Exception {
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre("1", "new role name", null, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void shouldCreate_forAlreadyExistedName() throws Exception {
        var region = new Region();
        region.setId(3L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(new ExamCentre("20", "EC1", region, null, null)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(10)
    void shouldUpdate_forValidDataAndValidId() throws Exception {
        var user = new AppUser();
        user.setEmail("change@email.com");
        user.setMobileNumber("8132817610");
        var examCentre = new ExamCentre("1", "new EC1", null, null, user);
        examCentre.setId(1L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(examCentre))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    void shouldNotUpdate_forInvalidEmailAndValidId() throws Exception {
        var user = new AppUser();
        user.setEmail("change@email");
        user.setMobileNumber("8132817610");

        var examCentre = new ExamCentre("1", "new EC1", null, null, user);
        examCentre.setId(1L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(examCentre))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    void shouldUpdate_forValidNameAndValidId() throws Exception {
        var examCentre = new ExamCentre(null, "Valid Name EC1", null, null, null);
        examCentre.setId(1L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(examCentre))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(13)
    void shouldNotUpdate_forValidDataAndInvalidId() throws Exception {
        var examCentre = new ExamCentre("50", "incorrect id", null, null, null);
        examCentre.setId(50L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(examCentre))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    void shouldNotUpdate_forInvalidRegionIdValidIdData() throws Exception {
        var region = new Region();
        region.setId(10L);
        var examCentre = new ExamCentre("50", "incorrect region id", region, null, null);
        examCentre.setId(50L);
        mockMvc.perform(post("/exam-centres/create")
                        .content(objectMapper.writeValueAsString(examCentre))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}