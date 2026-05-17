package com.innowise.userservice.mapper;

import com.innowise.userservice.model.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.innowise.userservice.dto.PaymentCardRequestDto;
import com.innowise.userservice.dto.PaymentCardResponseDto;


@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    @Mapping(source = "user.id", target = "userId")
    PaymentCardResponseDto toDto(PaymentCard paymentCard);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard toEntity(PaymentCardRequestDto dto);
}
