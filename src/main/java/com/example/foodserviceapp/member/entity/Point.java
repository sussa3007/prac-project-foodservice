package com.example.foodserviceapp.member.entity;

import com.example.foodserviceapp.audit.Audit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Point extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    private int pointCount;

    public void setPointCount(int count) {
        this.pointCount += count;
    }
}
