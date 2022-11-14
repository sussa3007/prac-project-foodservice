package com.example.foodserviceapp.food.repository;

import com.example.foodserviceapp.food.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food,Long> {
    Optional<Food> findByFoodCode(String foodCode);

    Page<Food> findAllByOrderByFoodIdDesc(Pageable page);
}
