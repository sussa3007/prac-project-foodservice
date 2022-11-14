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

        @Valid
        @NotNull(message = "최소 하나의 옵션이 필요합니다.")
        private List<OptionDto> options;

        public Order postDtoToOrder() {
            List<Long> verify = orderFood.stream().map(OrderFoodDto::getFoodId).collect(Collectors.toList());
            if(verify.size() != verify.stream().distinct().count())
                throw new ServiceLogicException(ErrorCode.ORDER_DUPLICATE);
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
                                                    .build()
                                    )).collect(Collectors.toList())
                    )
                    .options(
                            options.stream()
                                    .map(Option::of).collect(Collectors.toList())
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
        private Order.Status orderStatus;

        public Order patchDtoToOrder() {
            return Order.builder().orderId(orderId).orderStatus(orderStatus).build();
        }
    }


    @Data
    @AllArgsConstructor
    public static class Response {
        private Long orderId;
        private String orderStatus;
        private MemberDto.Response member;
        private List<OrderFoodDto.Response> orderFoods;
        private List<OptionDto.Response> options;

        public static Response orderToResponseDto(Order order) {
            return new Response(
                    order.getOrderId(),
                    order.getOrderStatus().getMessage(),
                    MemberDto.Response.memberToResponseDto(order.getMember()),
                    order.getOrderFoods().stream()
                            .map(OrderFoodDto.Response::orderFoodToResponseDto)
                            .collect(Collectors.toList()),
                    order.getOptions().stream()
                            .map(OptionDto.Response::optionToResponse)
                            .collect(Collectors.toList())
            );
        }
        public static List<Response> orderListToResponseDtos(List<Order> orderList) {
            return orderList.stream()
                    .filter(order -> (order.getOrderStatus().getStatus()==1))
                    .map(OrderDto.Response::orderToResponseDto)
                    .collect(Collectors.toList());
        }

    }

}
