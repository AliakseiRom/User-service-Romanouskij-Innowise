package user_service.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import user_service.user_service.dto.PaymentCardRequestDto;
import user_service.user_service.dto.PaymentCardResponseDto;
import user_service.user_service.model.PaymentCard;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardResponseDto toDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard toEntity(PaymentCardRequestDto dto);
}
