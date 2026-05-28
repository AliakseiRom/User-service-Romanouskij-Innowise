package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.security.JwtUser;
import com.innowise.userservice.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardService paymentCardService;

    private JwtUser adminUser() {
        return new JwtUser(1L, "admin", "ADMIN");
    }

    private JwtUser normalUser() {
        return new JwtUser(1L, "user", "USER");
    }

    @Test
    void shouldCreatePaymentCardSuccessfully() {

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUser)
                    .thenReturn(adminUser());

            User user = new User();
            user.setId(1L);

            PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
            requestDto.setUserId(1L);
            requestDto.setNumber("1234567812345678");
            requestDto.setHolder("Alex Ivanov");
            requestDto.setExpirationDate(LocalDate.of(2027, 12, 31));
            requestDto.setActive(true);

            PaymentCard paymentCard = new PaymentCard();

            PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
            responseDto.setId(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(paymentCardRepository.countByUserId(1L)).thenReturn(0L);
            when(paymentCardMapper.toEntity(requestDto)).thenReturn(paymentCard);
            when(paymentCardRepository.save(paymentCard)).thenReturn(paymentCard);
            when(paymentCardMapper.toDto(paymentCard)).thenReturn(responseDto);

            PaymentCardResponseDto result =
                    paymentCardService.createPaymentCard(requestDto);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Test
    void shouldThrowExceptionWhenUserHasMoreThanFiveCards() {

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUser)
                    .thenReturn(adminUser());

            User user = new User();
            user.setId(1L);

            PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
            requestDto.setUserId(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(paymentCardRepository.countByUserId(1L)).thenReturn(6L);

            assertThrows(RuntimeException.class,
                    () -> paymentCardService.createPaymentCard(requestDto));

            verify(paymentCardRepository, never()).save(any());
        }
    }

    @Test
    void shouldGetPaymentCardById() {

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUser)
                    .thenReturn(adminUser());

            PaymentCard paymentCard = new PaymentCard();
            paymentCard.setId(1L);

            PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
            responseDto.setId(1L);

            when(paymentCardRepository.findById(1L))
                    .thenReturn(Optional.of(paymentCard));

            when(paymentCardMapper.toDto(paymentCard))
                    .thenReturn(responseDto);

            PaymentCardResponseDto result =
                    paymentCardService.getPaymentCardById(1L);

            assertEquals(1L, result.getId());
        }
    }

    @Test
    void shouldGetAllCards() {

        Pageable pageable = PageRequest.of(0, 5);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);

        Page<PaymentCard> page =
                new PageImpl<>(List.of(paymentCard));

        PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
        responseDto.setId(1L);

        when(paymentCardRepository.findAll(pageable)).thenReturn(page);
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(responseDto);

        Page<PaymentCardResponseDto> result =
                paymentCardService.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetCardsByUserId() {

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {

            mocked.when(SecurityUtils::getCurrentUser)
                    .thenReturn(adminUser());

            Pageable pageable = PageRequest.of(0, 5);

            User user = new User();
            user.setId(1L);

            PaymentCard paymentCard = new PaymentCard();
            paymentCard.setId(1L);

            Page<PaymentCard> page =
                    new PageImpl<>(List.of(paymentCard));

            PaymentCardResponseDto responseDto = new PaymentCardResponseDto();
            responseDto.setId(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(paymentCardRepository.findByUserId(1L, pageable)).thenReturn(page);
            when(paymentCardMapper.toDto(paymentCard)).thenReturn(responseDto);

            Page<PaymentCardResponseDto> result =
                    paymentCardService.getCardsByUserId(1L, pageable);

            assertEquals(1, result.getTotalElements());
        }
    }
}