package com.example.mappingprac.food.repository;

import com.example.mappingprac.food.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food,Long> {
    Optional<Food> findByFoodCode(String foodCode);

    Page<Food> findAllByOrderByFoodIdDesc(Pageable page);
}
