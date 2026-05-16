package user_service.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.user_service.dto.PaymentCardRequestDto;
import user_service.user_service.dto.PaymentCardResponseDto;
import user_service.user_service.service.PaymentCardService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<PaymentCardResponseDto> createPaymentCard(
            @RequestBody PaymentCardRequestDto paymentCardRequestDto
    ) {
        PaymentCardResponseDto responseDto = paymentCardService.createPaymentCard(paymentCardRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getPaymentCardById(@PathVariable Long id) {
        PaymentCardResponseDto responseDto = paymentCardService.getPaymentCardById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> getAllPaymentCards(Pageable pageable) {
        return new ResponseEntity<>(paymentCardService.getAllCards(pageable), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<PaymentCardResponseDto>> getPaymentCardsByUserId(
            @PathVariable Long id,
            Pageable pageable
    ) {
        return new ResponseEntity<>(paymentCardService.getCardsByUserId(id, pageable), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> updatePaymentCard(
            @PathVariable Long id,
            @RequestBody PaymentCardRequestDto paymentCardRequestDto
    ) {
        return new ResponseEntity<>(paymentCardService.updateCard(id, paymentCardRequestDto), HttpStatus.OK);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<PaymentCardResponseDto> activatePaymentCard(@PathVariable Long id) {
        return new ResponseEntity<>(paymentCardService.activateCard(id), HttpStatus.OK);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<PaymentCardResponseDto> deactivatePaymentCard(@PathVariable Long id) {
        return new ResponseEntity<>(paymentCardService.deactivateCard(id), HttpStatus.OK);
    }
}
