package com.example.foodserviceapp.order.entity;

import com.example.foodserviceapp.audit.Audit;
import com.example.foodserviceapp.exception.ErrorCode;
import com.example.foodserviceapp.exception.ServiceLogicException;
import com.example.foodserviceapp.member.entity.Member;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Order extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Order.Status orderStatus;

    @Column(nullable = false)
    private int totalCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderFood> orderFoods = new ArrayList<>();


    public int getOrderStatus() {
        return this.orderStatus.getStatus();
    }

    public String getOrderStatusMessage() {
        return this.orderStatus.getMessage();
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = Arrays.stream(Status.values()).filter(status -> status.getStatus() == orderStatus)
                .findFirst().orElseThrow(() -> new ServiceLogicException(ErrorCode.BAD_REQUEST));
    }

    public static Order.Status findOrderStatus(int orderStatus) {
        return Arrays.stream(Status.values()).filter(status -> status.getStatus() == orderStatus)
                .findFirst().orElseThrow(() -> new ServiceLogicException(ErrorCode.BAD_REQUEST));
    }

    public void addOrderfoods(OrderFood orderFood) {
        orderFoods.add(orderFood);
        orderFood.setOrder(this);
    }

    public void addTotalCount(int price) {
        this.totalCount += price;
    }

    @Getter
    public enum Status {
        ORDER_REQUEST(1, "주문 요청"),
        ORDER_CONFIRM(2, "주문 확정"),
        ORDER_COMPLETE(3, "주문 처리 완료"),
        ORDER_CANCEL(4, "주문 취소");

        private final int status;
        private final String message;


        Status(int status, String message) {
            this.status = status;
            this.message = message;
        }
    }

}
