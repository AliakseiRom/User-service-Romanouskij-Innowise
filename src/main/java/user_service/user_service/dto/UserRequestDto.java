package user_service.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private LocalDate birthDate;

    @Email
    @NotBlank
    private String email;

    private boolean active;
}
