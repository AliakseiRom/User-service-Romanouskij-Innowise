package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.exceptions.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;
import com.innowise.userservice.mapper.PaymentCardMapper;



import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Transactional
    public PaymentCardResponseDto createPaymentCard(PaymentCardRequestDto paymentCardRequestDto) {
        System.out.println("USER ID = " + paymentCardRequestDto.getUserId());
        User user = userRepository.findById(paymentCardRequestDto.getUserId()).get();

        long countCards = paymentCardRepository.countByUserId(user.getId());
        
        if (countCards > 5) {
            throw new PaymentCardLimitExceededException("User cannot have more than 5 cards");
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequestDto);
        paymentCard.setUser(user);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    public PaymentCardResponseDto getPaymentCardById(Long id) {
        Optional<PaymentCard> paymentCard = paymentCardRepository.findById(id);

        if(paymentCard.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + " not found");
        }

        return paymentCardMapper.toDto(paymentCard.get());
    }

    public Page<PaymentCardResponseDto> getAllCards(
            Pageable pageable
    ) {

        return paymentCardRepository.findAll(pageable)
                .map(paymentCardMapper::toDto);
    }

    public Page<PaymentCardResponseDto> getCardsByUserId(
            Long userId,
            Pageable pageable
    ) {

        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }

        return paymentCardRepository
                .findByUserId(userId, pageable)
                .map(paymentCardMapper::toDto);
    }

    @Transactional
    public PaymentCardResponseDto updateCard(
            Long id,
            PaymentCardRequestDto dto
    ) {

        Optional<PaymentCard> cardOptional = paymentCardRepository.findById(id);
        if (cardOptional.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }
        PaymentCard card = paymentCardMapper.toEntity(dto);

        PaymentCard cardToUpdate = cardOptional.get();
        cardToUpdate.setNumber(dto.getNumber());
        cardToUpdate.setHolder(dto.getHolder());
        cardToUpdate.setExpirationDate(dto.getExpirationDate());
        cardToUpdate.setActive(dto.isActive());
        return paymentCardMapper.toDto(cardToUpdate);

    }

    @Transactional
    public PaymentCardResponseDto activateCard(Long id) {
        Optional<PaymentCard> card = paymentCardRepository.findById(id);

        if(card.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }

        PaymentCard cardToActivate = card.get();
        cardToActivate.setActive(true);

        return paymentCardMapper.toDto(cardToActivate);
    }

    @Transactional
    public PaymentCardResponseDto deactivateCard(Long id) {
        Optional<PaymentCard> card = paymentCardRepository.findById(id);

        if(card.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }

        PaymentCard cardToActivate = card.get();
        cardToActivate.setActive(false);

        return paymentCardMapper.toDto(cardToActivate);
    }

    @Transactional
    public void deleteCard(Long id) {
        Optional<PaymentCard> card = paymentCardRepository.findById(id);
        if(card.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }

        paymentCardRepository.deleteById(id);
    }
}
