package com.innowise.userservice.integration;

import com.innowise.userservice.integration.config.TestSecurityConfig;
import com.innowise.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setup() {
        when(jwtService.isTokenValid(anyString())).thenReturn(true);
        when(jwtService.extractUserId(anyString())).thenReturn(1L);
        when(jwtService.extractRole(anyString())).thenReturn("ADMIN");
        when(jwtService.extractLogin(anyString())).thenReturn("admin");
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
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
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    void shouldGetUserById() throws Exception {

        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
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

        mockMvc.perform(get("/user/1")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void shouldUpdateUser() throws Exception {

        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
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
                        .header("Authorization", "Bearer test")
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
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeactivateAndActivateUser() throws Exception {

        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
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

        mockMvc.perform(put("/user/deactivate/1")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/user/activate/1")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUser() throws Exception {

        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
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

        mockMvc.perform(delete("/user/1")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isNoContent());
    }
}