package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author athisii
 * @version 1.0
 * @since 5/4/24
 */

@WebMvcTest(controllers = RoleController.class)
class RoleControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void validateInput() throws Exception {
        String role = objectMapper.writeValueAsString(createRole());
        MockHttpServletRequestBuilder request = post("/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(role);

        mvc.perform(request)
                .andDo(handler -> {
                    System.out.println("Status code: " + handler.getResponse().getStatus());
                    System.out.println("Response body: " + handler.getResponse().getContentAsString());
                });
    }

    private Role createRole() {
        Role role = new Role();
        role.setCode(1);
        role.setName("ADMIN");
        return role;
    }
}
