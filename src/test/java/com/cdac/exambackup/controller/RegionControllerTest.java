//package com.cdac.exambackup.controller;
//
//import com.cdac.exambackup.entity.Region;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class RegionControllerTest {
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    @Order(1)
//    void shouldReturnData_forValidId() throws Exception {
//        mockMvc.perform(get("/regions/{id}", 1))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(2)
//    void shouldFail_forInvalidId() throws Exception {
//        mockMvc.perform(get("/regions/{id}", 7))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(3)
//    void shouldCreate_forValidData() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(5, "new")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(4)
//    void shouldNotCreate_forInvalidData() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(0, " ")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(5)
//    void shouldNotCreate_forValidCodeAndInvalidName() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(5, null)))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(6)
//    void shouldNotCreate_forValidNameAndInvalidCode() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(null, "valid name")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(7)
//    void shouldNotCreate_forAlreadyExistedCode() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(1, "new region name")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(8)
//    void shouldNotCreate_forAlreadyExistedName() throws Exception {
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(new Region(8, "south")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(9)
//    void shouldUpdate_forValidDataAndValidId() throws Exception {
//        var region = new Region(1, "both fields valid");
//        region.setId(1L);
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(region))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(10)
//    void shouldUpdate_forValidNameAndValidId() throws Exception {
//        var region = new Region(null, "only name valid");
//        region.setId(1L);
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(region))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(11)
//    void shouldNotUpdate_forValidDataAndInvalidId() throws Exception {
//        var region = new Region(1, "incorrect id");
//        region.setId(6L);
//        mockMvc.perform(post("/regions/create")
//                        .content(objectMapper.writeValueAsString(region))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//}