package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;
import com.innowise.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PaymentCardResponseDto> createPaymentCard(
            @RequestBody PaymentCardRequestDto paymentCardRequestDto
    ) {
        PaymentCardResponseDto responseDto = paymentCardService.createPaymentCard(paymentCardRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PaymentCardResponseDto> getPaymentCardById(@PathVariable Long id) {
        PaymentCardResponseDto responseDto = paymentCardService.getPaymentCardById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentCardResponseDto>> getAllPaymentCards(Pageable pageable) {
        return new ResponseEntity<>(paymentCardService.getAllCards(pageable), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<PaymentCardResponseDto>> getPaymentCardsByUserId(
            @PathVariable Long id,
            Pageable pageable
    ) {
        return new ResponseEntity<>(paymentCardService.getCardsByUserId(id, pageable), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PaymentCardResponseDto> updatePaymentCard(
            @PathVariable Long id,
            @RequestBody PaymentCardRequestDto paymentCardRequestDto
    ) {
        return new ResponseEntity<>(paymentCardService.updateCard(id, paymentCardRequestDto), HttpStatus.OK);
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponseDto> activatePaymentCard(@PathVariable Long id) {
        return new ResponseEntity<>(paymentCardService.activateCard(id), HttpStatus.OK);
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponseDto> deactivatePaymentCard(@PathVariable Long id) {
        return new ResponseEntity<>(paymentCardService.deactivateCard(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
