package com.example.foodserviceapp.order.service;


import com.example.foodserviceapp.advice.JwtPagePoint;
import com.example.foodserviceapp.advice.JwtPoint;
import com.example.foodserviceapp.auth.JwtTokenizer;
import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.food.service.FoodService;
import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.service.MemberService;
import com.example.foodserviceapp.order.entity.Order;
import com.example.foodserviceapp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final FoodService foodService;

    private final JwtTokenizer jwtTokenizer;


//    @JwtPoint verifyOrder(order,email)의 findMember에서 검증
    public Order createOrder(Order order, String email) {
        verifyOrder(order,email);
        updateOption(order);
        updateTotalCount(order);
        Order saveOrder = orderRepository.save(order);
        updatePoint(saveOrder);
        order.getMember().addOrder(saveOrder);
        return saveOrder;
    }


    @JwtPoint
    public Order updateOrder(Order order,String email) {
        Order findOrder = verifiedOrderById(order.getOrderId());

        verifyOrderStep(findOrder,email);

        findOrder.setOrderStatus(order.getOrderStatus());
        return findOrder;
    }

    @Transactional(readOnly = true)
    @JwtPoint
    public Order findOrder(Long orderId, String email) {
        return verifiedOrderById(orderId);
    }


    @Transactional(readOnly = true)
    public Page<Order> findOrders(String email, int page, int size) {
        if (email.equals(adminEmail)) {
            return orderRepository.findAllByOrder(
                    PageRequest.of(page, size, Sort.by("orderId").descending()));
        }
        throw new ServiceLogicException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    @Transactional(readOnly = true)
//    @JwtPagePoint findMember에서 검증
    public Page<Order> findOrdersByMemberId(String email, Long memberId, int page, int size) {
        Member member = memberService.findMember(memberId, email);
        Page<Order> findOrders =
                orderRepository.findByMemberOrderByOrderIdDesc(member, PageRequest.of(page, size));
        return findOrders;
    }

    @JwtPoint
    public Order cancelOrder(Long orderId, String email) {
        Order order = verifiedOrderById(orderId);
        verifyOrderStep(order,email);
        order.setOrderStatus(4);
        return order;
    }

    /* 부가 기능, 검증 로직 */

    private void verifyOrderStep(Order findOrder,String email) {
        if (!email.equals(adminEmail)) {
            int status = findOrder.getOrderStatus();
            if(status >= 2) throw new ServiceLogicException(ErrorCode.ALREADY_CONFIRM_ORDER);
        }
    }
    private Order verifiedOrderById(Long orderId) {
        Optional<Order> findOrder = orderRepository.findById(orderId);
        return findOrder.orElseThrow(
                () -> new ServiceLogicException(ErrorCode.ORDER_NOT_FOUND)
        );
    }

    private void updateOption(Order order) {
        order.getOrderFoods()
                .forEach(orderFood -> {
                    orderFood.updateOptions(orderFood.getOptions());
                });
    }

    private void updatePoint(Order order) {
        order.getMember().getPoint().setPointCount(pointCalculate(order));
    }

    private int pointCalculate(Order order) {
        return order.getOrderFoods().stream()
                .mapToInt(d -> (foodService.findFood(d.getFood().getFoodId()).getPrice()/10))
                .sum();
    }

    private void verifyOrder(Order order,String email) {
        Member member = memberService.findMember(order.getMember().getMemberId(),email);
        order.setMember(member);
        order.getOrderFoods()
                .forEach(orderFood ->{
                    orderFood.setFood(
                            foodService.findFood(orderFood.getFood().getFoodId()));
                    orderFood.setOrder(order);
                });
    }

    private void updateTotalCount(Order order) {
        order.getOrderFoods()
                .forEach(
                        orderFood -> order.addTotalCount(orderFood.getPrice())
                );
    }
}
