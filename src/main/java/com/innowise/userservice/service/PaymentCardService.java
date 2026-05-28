package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;
import com.innowise.userservice.exceptions.AccessDeniedException;
import com.innowise.userservice.exceptions.EntityNotFoundException;
import com.innowise.userservice.exceptions.PaymentCardLimitExceededException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.security.JwtUser;
import com.innowise.userservice.security.SecurityUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Transactional
    public PaymentCardResponseDto createPaymentCard(PaymentCardRequestDto paymentCardRequestDto) {
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        if (currentUser.getRole().equals("USER")) {

            paymentCardRequestDto.setUserId(currentUser.getUserId());
        }

        User user = userRepository.findById(paymentCardRequestDto.getUserId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with id " +
                                        paymentCardRequestDto.getUserId() +
                                        " not found"
                        )
                );

        long countCards = paymentCardRepository.countByUserId(user.getId());

        if (countCards >= 5) {
            throw new PaymentCardLimitExceededException("User cannot have more than 5 cards");
        }

        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequestDto);
        paymentCard.setUser(user);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    public PaymentCardResponseDto getPaymentCardById(Long id) {
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment card with id " + id + " not found"
                        )
                );

        if (currentUser.getRole().equals("USER")) {
            Long ownerId = card.getUser().getId();
            if (!currentUser.getUserId().equals(ownerId)) {
                throw new AccessDeniedException("Access denied");
            }
        }

        return paymentCardMapper.toDto(card);
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

        JwtUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole().equals("USER") && !currentUser.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

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
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        Optional<PaymentCard> cardOptional = paymentCardRepository.findById(id);
        if (cardOptional.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }

        PaymentCard cardToUpdate = cardOptional.get();
        if (currentUser.getRole().equals("USER")) {
            Long ownerId = cardToUpdate.getUser().getId();
            if (!currentUser.getUserId().equals(ownerId)) {
                throw new AccessDeniedException("Access denied");
            }
        }
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
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        Optional<PaymentCard> card = paymentCardRepository.findById(id);
        if(card.isEmpty()) {
            throw new EntityNotFoundException("Payment card with id " + id + " not found");
        }
        if (currentUser.getRole().equals("USER")) {
            Long ownerId = card.get().getUser().getId();
            if (!currentUser.getUserId().equals(ownerId)) {
                throw new AccessDeniedException("Access denied");
            }
        }

        paymentCardRepository.deleteById(id);
    }
}