package com.example.foodserviceapp.food.controller;

import com.example.foodserviceapp.dto.PageResponseDto;
import com.example.foodserviceapp.dto.ResponseDto;
import com.example.foodserviceapp.food.dto.FoodDto;
import com.example.foodserviceapp.food.entity.Food;
import com.example.foodserviceapp.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/foods")
@Validated
public class FoodController {

    private final FoodService foodService;


    @PostMapping
    public ResponseEntity postFood(
            @Valid @RequestBody FoodDto.Post request
            ) {
        Food food = request.postDtoToFood();
        Food createFood = foodService.createFood(food);
        return new ResponseEntity<>(
                ResponseDto.of(FoodDto.Response.foodToResponseDto(createFood)),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{food-id}")
    public ResponseEntity patchFood(
            @PathVariable("food-id") @Positive Long foodId,
            @Valid @RequestBody FoodDto.Patch patch
    ) {
        patch.setFoodId(foodId);
        Food updateFood = foodService.updateFood(patch.patchDtoToFood());
        return new ResponseEntity<>(
                ResponseDto.of(FoodDto.Response.foodToResponseDto(updateFood)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{food-id}")
    public ResponseEntity getFood(
            @PathVariable("food-id") @Positive Long foodId
    ) {
        Food findFood = foodService.findFood(foodId);
        return new ResponseEntity<>(
                ResponseDto.of(FoodDto.Response.foodToResponseDto(findFood)),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity getFoods(
            @RequestParam @Positive int page,
            @RequestParam @Positive int size
    ) {
        Page<Food> foodPage = foodService.findFoods(page-1, size);
        List<FoodDto.Response> responseList =
                FoodDto.Response.foodListToResponseDtos(foodPage.getContent());
        return new ResponseEntity<>(
                PageResponseDto.of(responseList,foodPage),
                HttpStatus.OK
        );
    }

    /* Food의 Description 검색 기능 구현*/
    @GetMapping("/detail")
    public ResponseEntity getFoodsDescription(
            @RequestParam String description,
            @RequestParam @Positive int page,
            @RequestParam @Positive int size
    ) {
        Page<Food> foodPage = foodService.findFoodsByDescription(description,page-1, size);
        List<FoodDto.Response> responseList =
                FoodDto.Response.foodListToResponseDtos(foodPage.getContent());
        return new ResponseEntity<>(
                PageResponseDto.of(responseList,foodPage),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{food-id}")
    public ResponseEntity deleteFood(
            @PathVariable @Positive Long foodId
    ) {
        foodService.deleteFood(foodId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }







    /* Test Data 생성 */
    @GetMapping("/test")
    public ResponseEntity testPost() {
        Food food1 = createTestFood(1L, "AAA");
        Food food2 = createTestFood(2L, "BBB");
        Food food3 = createTestFood(3L, "CCC");
        foodService.createFood(food1);
        foodService.createFood(food2);
        foodService.createFood(food3);

        return new ResponseEntity<>("Good",HttpStatus.OK);
    }
    private Food createTestFood(Long foodId,String foodCode) {
        return Food.builder()
                .name("Cheese Bugger"+foodId)
                .price((int) (4000+foodId))
                .description("쇠고기 패티와 오리지널 아메리칸 치즈"+foodId)
                .foodCode(foodCode)
                .foodStatus(Food.FoodStatus.FOOD_FOR_SALE)
                .build();
    }


}
