package com.ssackthree.ssackthree_back.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_town_product")
public class MyTownProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_town_product_id")
    private long id;

    private String title;
    private int price;
    private String content;
    private String hopingPlaceAddress;

    @OneToMany(mappedBy = "myTownProductEntity")
    private List<MyTownProductFileEntity> myTownProductFileEntityList;

    @OneToOne(mappedBy = "myTownProductEntity")
    private MyTownProductHopingPlaceEntity myTownProductHopingPlace;

    @OneToOne(mappedBy = "myTownProductEntity")
    private MyTownProductStatusEntity myTownProductStatusEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

}
