package com.example.foodserviceapp.order.dto;

import com.example.foodserviceapp.order.entity.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class OptionDto {

    @NotNull
    private Long foodId;
    @Range(min = 0, max = 3)
    private int optionNumber;
    @Range(min = 0, max = 5)
    private int optionCount;

    @Data
    @AllArgsConstructor
    public static class Response {
        private Long foodId;
        private String option;
        private int optionCount;

        public static Response optionToResponse(Option op) {
            return new Response(
                    op.getFoodId(),
                    op.getStatus().getMessage(),
                    op.getOptionCount()
            );

        }
    }
}
