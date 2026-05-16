package user_service.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardRequestDto {

    @NotBlank
    private String number;

    @NotBlank
    private String holder;

    @NotBlank
    private LocalDate expirationDate;

    @NotNull
    private Long userId;

    private boolean active;
}
