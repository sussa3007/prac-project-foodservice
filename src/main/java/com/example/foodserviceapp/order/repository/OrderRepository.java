package com.example.foodserviceapp.order.repository;

import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Page<Order> findAllByOrderByOrderIdDesc(Pageable pageable);

    Page<Order> findByMemberOrderByOrderIdDesc(Member member, Pageable pageable);
}
