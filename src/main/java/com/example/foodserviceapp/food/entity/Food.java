package com.example.foodserviceapp.food.entity;

import com.example.foodserviceapp.audit.Audit;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private int price;

    @Column(nullable = false)
    @Setter
    private String description;

    @Column(nullable = false)
    @Setter
    private String foodCode;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Setter
    private FoodStatus foodStatus = FoodStatus.FOOD_FOR_SALE;

    public enum FoodStatus {
        FOOD_FOR_SALE("판매중"),
        FOOD_SOLD_OUT("품절");
        @Getter
        private String status;

        FoodStatus(String status) {
            this.status = status;
        }

    }


}
