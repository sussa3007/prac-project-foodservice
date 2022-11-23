package com.example.foodserviceapp.food.repository;

import com.example.foodserviceapp.food.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food,Long> {
    Optional<Food> findByFoodCode(String foodCode);

    Page<Food> findAllByOrderByFoodIdDesc(Pageable page);
    /* Food의 Description 검색 기능 구현*/
    @Query(value = "SELECT f FROM Food f WHERE f.description LIKE %:description%",
            countQuery = "SELECT COUNT(f) FROM Food f WHERE f.description LIKE %:description%")
    Page<Food> findByDescriptionLike(String description, Pageable pageable);
}
