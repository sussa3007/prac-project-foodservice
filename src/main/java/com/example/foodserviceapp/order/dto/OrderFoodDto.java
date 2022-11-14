package com.example.foodserviceapp.order.dto;

import com.example.foodserviceapp.food.dto.FoodDto;
import com.example.foodserviceapp.order.entity.OrderFood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class OrderFoodDto {
    @Positive
    private Long foodId;
    @Positive
    private int quantity;

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long orderFoodId;
        private int quantity;
        private FoodDto.Response food;

        public static Response orderFoodToResponseDto(OrderFood orderFood) {
            return new Response(
                    orderFood.getOrderFoodId(),
                    orderFood.getQuantity(),
                    FoodDto.Response.foodToResponseDto(orderFood.getFood())
            );
        }
    }

}
