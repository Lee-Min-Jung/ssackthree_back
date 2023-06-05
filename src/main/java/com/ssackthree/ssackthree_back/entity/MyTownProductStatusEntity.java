package com.ssackthree.ssackthree_back.entity;

import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.enums.TownProductStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_town_product_status")
public class MyTownProductStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_product_status_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private TownProductStatusEnum productStatus;

    @OneToOne
    @JoinColumn(name = "my_town_product_id")
    private MyTownProductEntity myTownProductEntity;
}
