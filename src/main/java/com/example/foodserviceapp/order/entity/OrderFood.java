package com.example.foodserviceapp.order.entity;

import com.example.foodserviceapp.audit.Audit;
import com.example.foodserviceapp.food.entity.Food;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @OneToMany(mappedBy = "orderfood",cascade = CascadeType.ALL)
    private List<Option> options = new ArrayList<>();

    public List<Option> updateOptions(List<Option> options) {
        options.forEach(option -> option.setOrderfood(this));
        return options;
    }
    public static OrderFood of(
            int quantity,
            Food food,
            List<Option> options
    ) {
        return OrderFood.builder()
                .quantity(quantity)
                .food(food)
                .options(options)
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
