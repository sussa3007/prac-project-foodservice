package com.example.mappingprac.order.entity;

import com.example.mappingprac.audit.Audit;
import com.example.mappingprac.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor
public class Order extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderFood> orderFoods = new ArrayList<>();
}
