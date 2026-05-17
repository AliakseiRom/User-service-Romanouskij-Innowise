package com.innowise.userservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry
    ) {

        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Alex",
                                    "surname":"Ivanov",
                                    "birthDate":"2000-01-01",
                                    "email":"alex@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alex"))
                .andExpect(jsonPath("$.surname").value("Ivanov"))
                .andExpect(jsonPath("$.email").value("alex@test.com"));
    }

    @Test
    void shouldGetUserById() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"John",
                                    "surname":"Doe",
                                    "birthDate":"1999-01-01",
                                    "email":"john@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void shouldUpdateUser() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Old",
                                    "surname":"User",
                                    "birthDate":"2000-01-01",
                                    "email":"old@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Updated",
                                    "surname":"UpdatedSurname",
                                    "birthDate":"2000-01-01",
                                    "email":"updated@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.surname").value("UpdatedSurname"));
    }

    @Test
    void shouldDeactivateAndActivateUser() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Active",
                                    "surname":"User",
                                    "birthDate":"2000-01-01",
                                    "email":"active@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/user/deactivate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(put("/user/activate/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldDeleteUser() throws Exception {

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Delete",
                                    "surname":"User",
                                    "birthDate":"2000-01-01",
                                    "email":"delete@test.com",
                                    "active":true
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());
    }
}