package com.innowise.userservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentCardResponseDto implements Serializable {

    private Long id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private boolean active;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
