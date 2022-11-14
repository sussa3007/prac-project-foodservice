package com.example.foodserviceapp.food.service;

import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.food.entity.Food;
import com.example.foodserviceapp.food.repository.FoodRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {
    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    @Test
    @DisplayName("정상적인 음식 등록")
    void createFoodTest() {
        // Given
        Long foodId = 1L;
        Food testFood = createTestFood(foodId);
        when(foodRepository.findByFoodCode(anyString())).thenReturn(Optional.empty());
        given(foodRepository.save(any(Food.class))).willReturn(testFood);
        // When
        Food result = foodService.createFood(Food.builder().foodCode("PreventNull").build());
        // Then
        Assertions.assertEquals(result.getFoodId(),foodId);
        Assertions.assertEquals(result.getFoodCode(),testFood.getFoodCode());
        Assertions.assertEquals(result.getFoodStatus(),testFood.getFoodStatus());
    }

    @Test
    @DisplayName("예외 -음식 중복 등록")
    void createFoodExceptionTest() {
        // Given
        Long foodId = 1L;
        Food testFood = createTestFood(foodId);
        // When
        when(foodRepository.findByFoodCode(anyString())).thenReturn(Optional.of(testFood));
        // Then
        Assertions.assertThrows(
                //TODO 비즈니스 예외 적용하면 테스트 변경
                ServiceLogicException.class,
                () -> foodService.createFood(
                        Food.builder().foodCode("PreventNull").build()));

    }

    @Test
    @DisplayName("음식 정보 수정 로직 검증")
    void updateFoodTest() {
        // Given
        Long foodId = 1L;
        Food initFood = Food.builder()
                .foodId(foodId)
                .name("ChangeFoodName")
                .foodStatus(null)
                .build();
        Food testFood = createTestFood(foodId);
        // When
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(testFood));
        Food resultFood = foodService.checkFoodField(initFood);
        // Then
        Assertions.assertEquals(initFood.getName(),resultFood.getName());
        Assertions.assertEquals(testFood.getDescription(),resultFood.getDescription());
        Assertions.assertEquals(testFood.getPrice(),resultFood.getPrice());
    }

    @Test
    @DisplayName("예외 - 등록되지 않은 음식 단일 조회")
    void findFoodVerifiedTest() {
        // Given
        Long foodId = 1L;
        // When
        given(foodRepository.findById(anyLong())).willReturn(Optional.empty());
        // Then
        Assertions.assertThrows(
                ServiceLogicException.class,
                () -> foodService.findFood(foodId)
        );
    }

    @Test
    @DisplayName("등록된 음식 단일 조회")
    void findFood() {
        // Given
        Long foodId = 1L;
        Food testFood = createTestFood(foodId);
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(testFood));
        // When
        Food resultFood = foodService.findFood(foodId);
        // Then
        Assertions.assertEquals(testFood.getPrice(),resultFood.getPrice());
        Assertions.assertEquals(testFood.getDescription(),resultFood.getDescription());
    }

    private Food createTestFood(Long foodId) {
        return Food.builder()
                .foodId(foodId)
                .name("Cheese Bugger")
                .price(4000)
                .description("쇠고기 패티와 오리지널 아메리칸 치즈")
                .foodCode("CHB")
                .build();
    }

}