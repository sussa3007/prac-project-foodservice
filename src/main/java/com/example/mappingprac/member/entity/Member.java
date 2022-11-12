package com.example.mappingprac.member.entity;

import com.example.mappingprac.audit.Audit;
import com.example.mappingprac.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToMany(mappedBy = "member")
    private List<Order> order = new ArrayList<>();
}
