package com.example.foodserviceapp.order.dto;

import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.food.entity.Food;
import com.example.foodserviceapp.member.dto.MemberDto;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.order.entity.Option;
import com.example.foodserviceapp.order.entity.Order;
import com.example.foodserviceapp.order.entity.OrderFood;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class OrderDto {

    @Data
    @AllArgsConstructor
    public static class Post {
        @Positive
        private Long memberId;

        @Valid
        @NotNull(message = "주문 음식 정보는 필수입니다.")
        private List<OrderFoodDto> orderFood;

        /* 주문한 음식과 옵션의 음식이 동일한지 검증 */
        private void validateFoodId(OrderFoodDto orderFoodDto) {
            Long foodId = orderFoodDto.getFoodId();
            int size = orderFoodDto.getOptions().size();
            List<OptionDto> validateList = orderFoodDto.getOptions()
                    .stream()
                    .filter(optionDto -> Objects.equals(optionDto.getFoodId(), foodId))
                    .collect(Collectors.toList());
            if(size != validateList.size())
                throw new ServiceLogicException(ErrorCode.WRONG_FOOD_IN_OPTION);
        }
        /* 하나의 주문에 동일한 음식이 주문되었는지 검증 */
        private void validateDuplicateFood(List<OrderFoodDto> orderFoods) {
            orderFoods.forEach(this::validateFoodId);
            List<Long> verify = orderFoods.stream().map(OrderFoodDto::getFoodId).collect(Collectors.toList());
            if(verify.size() != verify.stream().distinct().count())
                throw new ServiceLogicException(ErrorCode.ORDER_DUPLICATE);
        }

        public Order postDtoToOrder() {
            validateDuplicateFood(orderFood);

            Member member = Member.builder()
                    .memberId(this.memberId)
                    .build();
            return Order.builder()
                    .member(member)
                    .orderFoods(
                            orderFood.stream()
                                    .map(dto -> OrderFood.of(
                                            dto.getQuantity(),
                                            Food.builder()
                                                    .foodId(dto.getFoodId())
                                                    .build(),
                                            dto.getOptions().stream()
                                                    .map(Option::of).collect(Collectors.toList())
                                    )).collect(Collectors.toList())
                    )
                    .orderStatus(Order.Status.ORDER_REQUEST)
                    .build();

        }
    }

    @Data
    @AllArgsConstructor
    public static class Patch {
        private Long orderId;
        @NotNull
        private int orderStatus;

        public Order patchDtoToOrder() {
            Order.Status findStatus = Order.findOrderStatus(orderStatus);

            return Order.builder()
                    .orderId(orderId)
                    .orderStatus(findStatus)
                    .build();
        }
    }


    @Data
    @AllArgsConstructor
    public static class Response {
        private Long orderId;
        private String orderStatus;
        private int totalCount;
        private MemberDto.Response member;
        private List<OrderFoodDto.Response> orderFoods;

        public static Response orderToResponseDto(Order order) {
            return new Response(
                    order.getOrderId(),
                    order.getOrderStatusMessage(),
                    order.getTotalCount(),
                    MemberDto.Response.memberToResponseDto(order.getMember()),
                    order.getOrderFoods().stream()
                            .map(OrderFoodDto.Response::orderFoodToResponseDto)
                            .collect(Collectors.toList())
            );
        }
        public static List<Response> orderListToResponseDtos(List<Order> orderList) {
            return orderList.stream()
                    .filter(order -> (order.getOrderStatus()==1))
                    .map(OrderDto.Response::orderToResponseDto)
                    .collect(Collectors.toList());
        }

    }

}
