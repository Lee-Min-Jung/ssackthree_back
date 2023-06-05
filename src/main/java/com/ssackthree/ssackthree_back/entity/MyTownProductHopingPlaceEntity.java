package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "tb_town_product_hoping_place")
public class MyTownProductHopingPlaceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_product_hoping_place_id")
    private long id;

    private double latitude;

    private double longitude;

    @OneToOne
    @JoinColumn(name = "my_town_product_id")
    private MyTownProductEntity myTownProductEntity;
}
