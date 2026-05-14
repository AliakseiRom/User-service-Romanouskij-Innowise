package user_service.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import user_service.user_service.dto.PaymentCardResponseDto;
import user_service.user_service.dto.UserResponseDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldCreateCardSuccessfully() throws Exception {

        Long userId = createUser();

        mockMvc.perform(post("/card")
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.holder").value("Alex Ivanov"));
    }

    @Test
    void shouldGetCardById() throws Exception {

        Long userId = createUser();

        Long cardId = createCard(userId);

        mockMvc.perform(get("/card/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("Alex Ivanov"));
    }

    @Test
    void shouldUpdateCard() throws Exception {

        Long userId = createUser();

        Long cardId = createCard(userId);

        mockMvc.perform(put("/card/" + cardId)
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder")
                        .value("Updated Holder"));
    }

    @Test
    void shouldDeactivateAndActivateCard() throws Exception {

        Long userId = createUser();

        Long cardId = createCard(userId);

        mockMvc.perform(put("/card/deactivate/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(put("/card/activate/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldDeleteCard() throws Exception {

        Long userId = createUser();

        Long cardId = createCard(userId);

        mockMvc.perform(delete("/card/" + cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetCardsByUserId() throws Exception {

        Long userId = createUser();

        createCard(userId);

        mockMvc.perform(get("/card/user/" + userId))
                .andExpect(status().isOk());
    }

    private Long createUser() throws Exception {

        String response = mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name":"Alex",
                                    "surname":"Ivanov",
                                    "birthDate":"2000-01-01",
                                    "email":"alex%s@test.com",
                                    "active":true
                                }
                                """.formatted(System.currentTimeMillis())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponseDto dto =
                objectMapper.readValue(
                        response,
                        UserResponseDto.class
                );

        return dto.getId();
    }

    private Long createCard(Long userId) throws Exception {

        String response = mockMvc.perform(post("/card")
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
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentCardResponseDto dto =
                objectMapper.readValue(
                        response,
                        PaymentCardResponseDto.class
                );

        return dto.getId();
    }
}