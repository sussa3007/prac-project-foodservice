package com.example.mappingprac.order.entity;

import com.example.mappingprac.audit.Audit;
import com.example.mappingprac.food.entity.Food;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

}
