package com.example.foodserviceapp.order.dto;

import com.example.foodserviceapp.food.dto.FoodDto;
import com.example.foodserviceapp.order.entity.OrderFood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class OrderFoodDto {
    @Positive
    private Long foodId;
    @Positive
    private int quantity;
    @Valid
    @NotNull(message = "최소 하나의 옵션이 필요합니다.")
    private List<OptionDto> options;



    @Data
    @AllArgsConstructor
    public static class Response {
        private Long orderFoodId;
        private int quantity;
        private FoodDto.ResponseForOrder food;
        private List<OptionDto.Response> options;

        public static Response orderFoodToResponseDto(OrderFood orderFood) {
            return new Response(
                    orderFood.getOrderFoodId(),
                    orderFood.getQuantity(),
                    FoodDto.ResponseForOrder.foodToResponseForOrderDto(orderFood.getFood()),
                    orderFood.getOptions().stream()
                            .map(OptionDto.Response::optionToResponse)
                            .collect(Collectors.toList())
            );
        }
    }

}
