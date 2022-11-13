package com.example.foodserviceapp;

import com.example.foodserviceapp.member.entity.Member;
import com.example.foodserviceapp.member.entity.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;

@Slf4j
//@Configuration
public class JpaMain {
    private EntityManager em;
    private EntityTransaction tx;

//    @Bean
    public CommandLineRunner testJpaBasicRunner(EntityManagerFactory emFactory) {
        this.em = emFactory.createEntityManager();
        this.tx = em.getTransaction();

        return args -> {
            tx.begin();
            Member member = new Member();
            member.setName("abc");
            member.setEmail("aaa@gmail.com");
            member.setPhone("010-5555-5555");
            Member member1 = new Member();
            member1.setName("bbb");
            member1.setEmail("bbb@gmail.com");
            member1.setPhone("010-8888-5555");
            Member member2 = new Member();
            member2.setName("ccc");
            member2.setEmail("ccc@gmail.com");
            member2.setPhone("010-3333-5555");
            Member member3 = new Member();
            member3.setName("ddd");
            member3.setEmail("ddd@gmail.com");
            member3.setPhone("010-4444-5555");
            Member member4 = new Member();
            member4.setName("eee");
            member4.setEmail("eee@gmail.com");
            member4.setPhone("010-1234-5555");
            Point point = new Point();
            Point point1 = new Point();
            Point point2 = new Point();
            Point point3 = new Point();
            Point point4 = new Point();
            point.setPointCount(5);
            point1.setPointCount(4);
            point2.setPointCount(3);
            point3.setPointCount(2);
            point4.setPointCount(1);
            member.setPoint(point);
            member1.setPoint(point1);
            member2.setPoint(point2);
            member3.setPoint(point3);
            member4.setPoint(point4);


            em.persist(member);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);
            tx.commit();
            em.clear();

            tx.begin();
            List<Member> members = em
                    .createQuery("select m from Member m", Member.class)
                    .getResultList();
            tx.commit();

        };
    }
}

