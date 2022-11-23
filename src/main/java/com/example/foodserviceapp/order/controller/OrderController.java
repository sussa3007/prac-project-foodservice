package com.example.foodserviceapp.order.controller;

import com.example.foodserviceapp.auth.details.MemberDetailsService;
import com.example.foodserviceapp.dto.PageResponseDto;
import com.example.foodserviceapp.dto.ResponseDto;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.order.dto.OrderDto;
import com.example.foodserviceapp.order.entity.Order;
import com.example.foodserviceapp.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity postOrder(
            @RequestBody @Valid OrderDto.Post request,
            Principal principal
    ) {
        String email = principal.getName();
        Order order = request.postDtoToOrder();
        Order createdOrder = orderService.createOrder(order, email);
        OrderDto.Response response = OrderDto.Response.orderToResponseDto(createdOrder);
        return new ResponseEntity<>(ResponseDto.of(response), HttpStatus.CREATED);
    }

    @PatchMapping("/{order-id}")
    public ResponseEntity patchOrder(
            @PathVariable("order-id") @Positive Long orderId,
            @RequestBody @Valid OrderDto.Patch request,
            Principal principal
    ) {
        String email = principal.getName();
        request.setOrderId(orderId);
        Order order = orderService.updateOrder(request.patchDtoToOrder(), email);
        OrderDto.Response response = OrderDto.Response.orderToResponseDto(order);
        return new ResponseEntity<>(ResponseDto.of(response), HttpStatus.OK);
    }

    @GetMapping("/{order-id}")
    public ResponseEntity getOrder(
            @PathVariable("order-id") @Positive Long orderId,
            Principal principal
    ) {
        String email = principal.getName();
        Order order = orderService.findOrder(orderId, email);
        OrderDto.Response response = OrderDto.Response.orderToResponseDto(order);
        return new ResponseEntity<>(ResponseDto.of(response), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getOrders(
            @RequestParam @Positive int page,
            @RequestParam @Positive int size
    ) {
        Page<Order> orderPage = orderService.findOrders(page - 1, size);
        List<OrderDto.Response> responseList =
                OrderDto.Response.orderListToResponseDtos(orderPage.getContent());
        return new ResponseEntity<>(PageResponseDto.of(responseList, orderPage), HttpStatus.OK);
    }

    /* 지정 Member의 Order List 조회 */
    @GetMapping("/{member-id}/orders")
    public ResponseEntity getOrdersByMember(
            @PathVariable("member-id") @Positive Long memberId,
            @RequestParam @Positive int page,
            @RequestParam @Positive int size
    ) {

        Page<Order> orderPage = orderService.findOrdersByMemberId(memberId, page - 1, size);
        List<OrderDto.Response> responseList =
                OrderDto.Response.orderListToResponseDtos(orderPage.getContent());
        return new ResponseEntity<>(PageResponseDto.of(responseList, orderPage), HttpStatus.OK);
    }

    @DeleteMapping("/{order-id}")
    public ResponseEntity deleteOrder(
            @PathVariable @Positive Long orderId,
            Principal principal
    ) {
        String email = principal.getName();
        orderService.cancelOrder(orderId,email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
