package com.example.foodserviceapp.order.repository;

import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends
        JpaRepository<Order,Long> {
    /* 지정 Member의 Order List 조회 */
    Page<Order> findByMemberOrderByOrderIdDesc(Member member, Pageable pageable);


    /**
     * TODO 두개 이상의 컬랙션은 페치조인으로 조회 하지 못한다.
     * 두개의 컬렉션에 페치 조인 할 경우 예외 정리
     * Option은 왜 해결되지 못하지?
     * @return
     */
    @Query("select o from ORDERS o " +
            "join fetch o.member m  " +
            "join fetch m.point pt " +
            "join fetch o.orderFoods od " +
//            "join fetch od.options op " +
            "join fetch od.food f " +
            "order by o.orderId desc")
    List<Order> findAllQuery();


    /* Entity Graph 적용시 OOM 경고 출력
    *  join fetch o.orderFoods od 추가해도 동일 현상 */
//    @EntityGraph(attributePaths = "orderFoods")
    @Query(value = "select o from ORDERS o " +
            "join fetch o.member m " +
            "join fetch m.point p",
            countQuery = "SELECT COUNT(o) FROM ORDERS o")
    Page<Order> findAllByOrder(Pageable pageable);

}
