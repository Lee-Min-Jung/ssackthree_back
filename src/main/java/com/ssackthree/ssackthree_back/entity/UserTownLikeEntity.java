package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@RequiredArgsConstructor
@Entity
@Table(name = "tb_user_town_like")
@AllArgsConstructor
public class UserTownLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_like_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "my_town_product_id")
    private MyTownProductEntity myTownProductEntity;


}
