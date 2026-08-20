package com.example.chook.payment.entity;
//상품테이블
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

//entity_yetdunguut/Product
@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    //몇번째 상품인지  ( 10000원 20000원 30000원 50000원
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    //구매시 얻을수있는 포인트양
    @Column(name = "point_get", nullable = false)
    private Integer pointGet;

    //가격 (할인은 넣으려면 넣을순 있을듯
    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    //제품명(이긴한데 사실상 몇번째 상품인지랑 같은 용도긴함.
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;
}
