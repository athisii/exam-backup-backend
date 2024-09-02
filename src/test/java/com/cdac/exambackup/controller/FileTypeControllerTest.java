//package com.cdac.exambackup.controller;
//
//import com.cdac.exambackup.entity.FileType;
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
//class FileTypeControllerTest {
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    @Order(1)
//    void shouldReturnData_forValidId() throws Exception {
//        mockMvc.perform(get("/file-types/{id}", 1))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(2)
//    void shouldFail_forInvalidId() throws Exception {
//        mockMvc.perform(get("/file-types/{id}", 30))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(3)
//    void shouldCreate_forValidData() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(10, "new")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(4)
//    void shouldNotCreate_forInvalidData() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(0, " ")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(5)
//    void shouldNotCreate_forValidCodeAndInvalidName() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(15, null)))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(6)
//    void shouldNotCreate_forValidNameAndInvalidCode() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(null, "VALID_NAME")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(7)
//    void shouldNotCreate_forAlreadyExistedCode() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(1, "new name file")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(8)
//    void shouldNotCreate_forAlreadyExistedName() throws Exception {
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(new FileType(8, "PXE_LOG")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @Order(9)
//    void shouldUpdate_forValidDataAndValidId() throws Exception {
//        var fileType = new FileType(1, "both fields valid");
//        fileType.setId(1L);
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(fileType))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(10)
//    void shouldUpdate_forValidNameAndValidId() throws Exception {
//        var fileType = new FileType(null, "only name valid");
//        fileType.setId(1L);
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(fileType))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @Order(11)
//    void shouldNotUpdate_forValidDataAndInvalidId() throws Exception {
//        var fileType = new FileType(1, "incorrect id");
//        fileType.setId(6L);
//        mockMvc.perform(post("/file-types/create")
//                        .content(objectMapper.writeValueAsString(fileType))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//}