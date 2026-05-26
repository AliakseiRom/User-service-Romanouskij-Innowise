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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class PaymentCardIntegrationTest {

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
    void shouldCreateCardSuccessfully() throws Exception {

        Long userId = createUser();

        mockMvc.perform(post("/card")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number":"1234567812345678",
                                    "holder":"Alex Ivanov",
                                    "expirationDate":"2027-12-31",
                                    "userId":%d,
                                    "active":true
                                }
                                """.formatted(userId)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetCardById() throws Exception {

        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(get("/card/" + cardId)
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCard() throws Exception {

        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(put("/card/" + cardId)
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number":"9999999999999999",
                                    "holder":"Updated Holder",
                                    "expirationDate":"2030-12-31",
                                    "userId":%d,
                                    "active":true
                                }
                                """.formatted(userId)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCard() throws Exception {

        Long userId = createUser();
        Long cardId = createCard(userId);

        mockMvc.perform(delete("/card/" + cardId)
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isNoContent());
    }

    private Long createUser() throws Exception {

        String response = mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Test",
                                    "surname":"User",
                                    "birthDate":"2000-01-01",
                                    "email":"test%s@test.com",
                                    "active":true
                                }
                                """.formatted(System.currentTimeMillis())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();
    }

    private Long createCard(Long userId) throws Exception {

        String response = mockMvc.perform(post("/card")
                        .header("Authorization", "Bearer test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "number":"1234567812345678",
                                    "holder":"Alex Ivanov",
                                    "expirationDate":"2027-12-31",
                                    "userId":%d,
                                    "active":true
                                }
                                """.formatted(userId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();
    }
}