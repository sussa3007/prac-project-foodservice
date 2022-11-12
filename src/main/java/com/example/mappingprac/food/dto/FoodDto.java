package com.example.mappingprac.food.dto;

import com.example.mappingprac.food.entity.Food;
import com.example.mappingprac.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FoodDto {

    @Data
    public static class Post {
        @NotBlank
        private String name;

        @Range(min = 500, max = 50000)
        private int price;

        @NotBlank
        @Size(min= 0, max = 100, message = "음식 설명은 100자이내 입니다.")
        private String description;

        @NotBlank
        @Pattern(regexp = "^([A-Za-z]){3}$",
                message = "음식 코드는 3자리 영문이어야 합니다.")
        private String foodCode;

        public Food postDtoToFood() {
            return Food.builder()
                    .name(this.getName())
                    .price(this.getPrice())
                    .description(this.getDescription())
                    .foodCode(this.getFoodCode())
                    .foodStatus(Food.FoodStatus.FOOD_FOR_SALE)
                    .build();
        }
    }

    @Data
    public static class Patch {

        private Long foodId;

        private String name;

        private int price;

        @Size(min= 0, max = 100, message = "음식 설명은 100자이내 입니다.")
        private String description;

        @Pattern(regexp = "^([A-Za-z]){3}$",
                message = "음식 코드는 3자리 영문이어야 합니다.")
        private String foodCode;

        private Food.FoodStatus foodStatus;

        public String getFoodStatus() {
            return foodStatus.getStatus();
        }
        public Food patchDtoToFood() {
            return Food.builder()
                    .foodId(foodId)
                    .name(name)
                    .price(price)
                    .description(description)
                    .foodCode(foodCode)
                    .foodStatus(foodStatus)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    public static class Response {

        private Long foodId;

        private String name;

        private int price;

        private String description;

        private String foodCode;

        private Food.FoodStatus foodStatus;

        private LocalDateTime createdAt;

        private LocalDateTime modifiedAt;

        public String getFoodStatus() {
            return foodStatus.getStatus();
        }
        public static Response foodToResponseDto(Food food) {
            return new FoodDto.Response(
                    food.getFoodId(),
                    food.getName(),
                    food.getPrice(),
                    food.getDescription(),
                    food.getFoodCode(),
                    food.getFoodStatus(),
                    food.getCreatedAt(),
                    food.getModifiedAt()
            );
        }

        public static List<Response> foodListToResponseDtos(List<Food> foods) {
            return foods.stream()
                    .map(Response::foodToResponseDto)
                    .collect(Collectors.toList());
        }


    }

}
