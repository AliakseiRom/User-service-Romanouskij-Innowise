package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
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
            throw new RuntimeException("User cannot have more than 5 cards");
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequestDto);
        paymentCard.setUser(user);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    public PaymentCardResponseDto getPaymentCardById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id).get();
        return paymentCardMapper.toDto(paymentCard);
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
        PaymentCard card = paymentCardMapper.toEntity(dto);

        if (cardOptional.isPresent()) {
            PaymentCard cardToUpdate = cardOptional.get();
            cardToUpdate.setNumber(dto.getNumber());
            cardToUpdate.setHolder(dto.getHolder());
            cardToUpdate.setExpirationDate(dto.getExpirationDate());
            cardToUpdate.setActive(dto.isActive());
            return paymentCardMapper.toDto(cardToUpdate);
        }

        return null;
    }

    @Transactional
    public PaymentCardResponseDto activateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id).get();

        card.setActive(true);

        return paymentCardMapper.toDto(card);
    }

    @Transactional
    public PaymentCardResponseDto deactivateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id).get();

        card.setActive(false);

        return paymentCardMapper.toDto(card);
    }

    @Transactional
    public void deleteCard(Long id) {
        paymentCardRepository.deleteById(id);
    }
}
