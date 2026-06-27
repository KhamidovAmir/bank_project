package ru.khan.bank.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest
        (
                @Schema(
                        description = "ID получателя",
                        example = "2"
                )
                @NotNull
                UUID accountPublicIdFrom,

                @Schema(
                        description = "ID получателя",
                        example = "2"
                )
                @NotNull
                UUID accountPublicIdTo,

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
        ) {}
