package com.example.foodserviceapp.order.entity;

import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.order.dto.OptionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Options status =Options.NONE_OPTIONS;

    @Column(nullable = false)
    private int optionCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_food_id")
    private OrderFood orderfood;

    @Column(nullable = false)
    private Long foodId;


    public static Option of(OptionDto optionDto) {
        Option option = new Option();
        Options findOptions = Arrays.stream(Options.values())
                .filter(options -> options.optionNumber == optionDto.getOptionNumber())
                .findAny().orElseThrow(() ->new ServiceLogicException(ErrorCode.OPTION_NOT_FOUND));
        option.setStatus(findOptions);
        option.setOptionCount(optionDto.getOptionCount());
        option.setFoodId(optionDto.getFoodId());
        return option;
    }

    @Getter
    public enum Options {
        NONE_OPTIONS(0, "옵션 없음"),
        ADD_PATTY(1, "패티 추가"),
        ADD_TOPPING(2, "토핑 추가"),
        ADD_SOURCE(3, "소스 추가");

        private int optionNumber;
        private String message;


        Options(int optionNumber, String message) {
            this.optionNumber = optionNumber;
            this.message = message;
        }
    }
}
