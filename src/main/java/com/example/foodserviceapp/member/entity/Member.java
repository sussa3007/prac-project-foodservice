package com.example.foodserviceapp.member.entity;

import com.example.foodserviceapp.audit.Audit;
import com.example.foodserviceapp.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE_MEMBER;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToMany(mappedBy = "member")
    private List<Order> order = new ArrayList<>();


    /* 연관관게 편의 메소드 */
    public void addPoint() {
        this.point = new Point();
    }

    public void addOrder(Order order) {
        this.order.add(order);
        order.setMember(this);
    }



    @Getter
    public enum MemberStatus {
        ACTIVE_MEMBER(1,"활동 회원"),
        DORMANT_MEMBER(2, "휴면 회원"),
        WITHDRAWAL_MEMBER(3,"탈퇴 회원");
        private int num;
        private String status;

        MemberStatus(int num, String status) {
            this.num = num;
            this.status = status;
        }
    }
}
