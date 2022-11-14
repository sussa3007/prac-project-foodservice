package com.example.foodserviceapp.order.entity;

import com.example.foodserviceapp.audit.Audit;
import com.example.foodserviceapp.food.entity.Food;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderFood extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderFoodId;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;

    public static OrderFood of(
            int quantity,
            Food food
    ) {
        return OrderFood.builder()
                .quantity(quantity)
                .food(food)
                .build();
    }
    public static OrderFood of(
            int quantity,
            Order order,
            Food food
    ) {
        return OrderFood.builder()
                .quantity(quantity)
                .order(order)
                .food(food)
                .build();
    }

}
