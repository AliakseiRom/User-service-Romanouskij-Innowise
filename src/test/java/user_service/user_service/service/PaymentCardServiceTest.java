package user_service.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import user_service.user_service.dto.PaymentCardRequestDto;
import user_service.user_service.dto.PaymentCardResponseDto;
import user_service.user_service.mapper.PaymentCardMapper;
import user_service.user_service.model.PaymentCard;
import user_service.user_service.model.User;
import user_service.user_service.repository.PaymentCardRepository;
import user_service.user_service.repository.UserRepository;

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

    @Test
    void shouldCreatePaymentCardSuccessfully() {

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

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(paymentCardRepository.countByUserId(1L))
                .thenReturn(0L);

        when(paymentCardMapper.toEntity(requestDto))
                .thenReturn(paymentCard);

        when(paymentCardRepository.save(paymentCard))
                .thenReturn(paymentCard);

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        PaymentCardResponseDto result =
                paymentCardService.createPaymentCard(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).findById(1L);
        verify(paymentCardRepository).countByUserId(1L);
        verify(paymentCardRepository).save(paymentCard);
    }

    @Test
    void shouldThrowExceptionWhenUserHasMoreThanFiveCards() {

        User user = new User();
        user.setId(1L);

        PaymentCardRequestDto requestDto = new PaymentCardRequestDto();
        requestDto.setUserId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(paymentCardRepository.countByUserId(1L))
                .thenReturn(6L);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentCardService.createPaymentCard(requestDto)
        );

        assertEquals(
                "User cannot have more than 5 cards",
                exception.getMessage()
        );

        verify(paymentCardRepository, never()).save(any());
    }

    @Test
    void shouldGetPaymentCardById() {

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);

        PaymentCardResponseDto responseDto =
                new PaymentCardResponseDto();

        responseDto.setId(1L);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(paymentCard));

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        PaymentCardResponseDto result =
                paymentCardService.getPaymentCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldGetAllCards() {

        Pageable pageable = PageRequest.of(0, 5);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);

        Page<PaymentCard> page =
                new PageImpl<>(List.of(paymentCard));

        PaymentCardResponseDto responseDto =
                new PaymentCardResponseDto();

        responseDto.setId(1L);

        when(paymentCardRepository.findAll(pageable))
                .thenReturn(page);

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        Page<PaymentCardResponseDto> result =
                paymentCardService.getAllCards(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetCardsByUserId() {

        Pageable pageable = PageRequest.of(0, 5);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);

        Page<PaymentCard> page =
                new PageImpl<>(List.of(paymentCard));

        PaymentCardResponseDto responseDto =
                new PaymentCardResponseDto();

        responseDto.setId(1L);

        when(paymentCardRepository.findByUserId(1L, pageable))
                .thenReturn(page);

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        Page<PaymentCardResponseDto> result =
                paymentCardService.getCardsByUserId(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldActivateCard() {

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);
        paymentCard.setActive(false);

        PaymentCardResponseDto responseDto =
                new PaymentCardResponseDto();

        responseDto.setActive(true);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(paymentCard));

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        PaymentCardResponseDto result =
                paymentCardService.activateCard(1L);

        assertTrue(paymentCard.isActive());
        assertTrue(result.isActive());
    }

    @Test
    void shouldDeactivateCard() {

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(1L);
        paymentCard.setActive(true);

        PaymentCardResponseDto responseDto =
                new PaymentCardResponseDto();

        responseDto.setActive(false);

        when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(paymentCard));

        when(paymentCardMapper.toDto(paymentCard))
                .thenReturn(responseDto);

        PaymentCardResponseDto result =
                paymentCardService.deactivateCard(1L);

        assertFalse(paymentCard.isActive());
        assertFalse(result.isActive());
    }

    @Test
    void shouldDeleteCard() {

        Long cardId = 1L;

        paymentCardService.deleteCard(cardId);

        verify(paymentCardRepository).deleteById(cardId);
    }
}
