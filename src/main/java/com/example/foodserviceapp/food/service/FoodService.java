package com.example.foodserviceapp.food.service;

import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.food.entity.Food;
import com.example.foodserviceapp.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public Food createFood(Food food) {
        String foodCode = food.getFoodCode().toUpperCase();
        verifyExistFood(foodCode);
        food.setFoodCode(foodCode);
        return foodRepository.save(food);
    }

    public Food updateFood(Food food) {
        return checkFoodField(food);
    }

    @Transactional(readOnly = true)
    public Food findFood(Long foodId) {
        return findVerifiedFoodById(foodId);
    }

    @Transactional(readOnly = true)
    public Page<Food> findFoods(int page, int size) {
        return foodRepository.findAllByOrderByFoodIdDesc(PageRequest.of(page, size));
    }

    /* Food의 Description 검색 기능 구현*/
    @Transactional(readOnly = true)
    public Page<Food> findFoodsByDescription(String description,int page, int size) {
        return foodRepository.findByDescriptionLike(description,PageRequest.of(page, size));
    }

    public void deleteFood(Long foodId) {
        Food verifiedFood = findVerifiedFoodById(foodId);
        foodRepository.deleteById(verifiedFood.getFoodId());
    }




    /* 검증 로직 */

    public void verifyExistFood(String foodCode) {
        Optional<Food> findFood = foodRepository.findByFoodCode(foodCode);
        if(findFood.isPresent())
            // todo businessException edit
            throw new ServiceLogicException(ErrorCode.FOOD_EXISTS);
    }

    public Food findVerifiedFoodById(Long foodId) {
        Optional<Food> findFood = foodRepository.findById(foodId);
        return findFood.orElseThrow(
                // todo businessException edit
                () -> new ServiceLogicException(ErrorCode.FOOD_NOT_FOUND)
        );
    }

    public Food checkFoodField(Food food) {
        Food findFood = findVerifiedFoodById(food.getFoodId());
        if(food.getPrice() != 0 ) findFood.setPrice(food.getPrice());
        Optional.ofNullable(food.getName())
                .ifPresent(findFood::setName);
        Optional.ofNullable(food.getDescription())
                .ifPresent(findFood::setDescription);
        Optional.ofNullable(food.getFoodCode())
                .ifPresent(findFood::setFoodCode);
        Optional.ofNullable(food.getFoodStatus())
                .ifPresent(findFood::setFoodStatus);
        findFood.setModifiedAt(LocalDateTime.now());
        return findFood;
    }
}
