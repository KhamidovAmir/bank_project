package ru.khan.bank.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Тело запроса на вывод средств")
public record WithdrawRequest
        (
                @Schema(
                        description = "Публичный ID счета формата UUID",
                        example = "2e2aef60-c2c7-4d3d-80d7-5fb4d3d73b92"
                )
                @NotNull
                UUID accountPublicId,

                @Schema(
                        description = "Сумма операция",
                        example = "1.00"
                )
                @NotNull
                @Positive
                @Digits(integer=19, fraction=2)
                BigDecimal amount,

                @Schema(
                        description = "Комментарий к операции"
                )
                String description
        )
{}
