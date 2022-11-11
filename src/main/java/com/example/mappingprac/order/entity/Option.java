package com.example.mappingprac.order.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    private Options options =Options.NONE_OPTIONS;

    @Column(nullable = false)
    private int optionCount;

    static private enum Options {
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
