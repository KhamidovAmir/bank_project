package ru.khan.bank.operation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.khan.bank.admin.dto.OperationsPageableResponse;
import ru.khan.bank.operation.dto.MoneyOperationsResponse;
import ru.khan.bank.operation.entity.MoneyOperation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MoneyOperationMapper {
    OperationsPageableResponse toOperationsPageableResponse(MoneyOperation moneyOperation);

    MoneyOperationsResponse toMoneyOperationsResponse(MoneyOperation moneyOperation);


}
