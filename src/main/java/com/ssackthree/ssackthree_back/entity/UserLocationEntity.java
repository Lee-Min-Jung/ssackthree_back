package com.ssackthree.ssackthree_back.entity;

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
@Table(name = "tb_user_location")
public class UserLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_locaion_id")
    private long id;

    private double latitude;
    private double longitude;
    private int m;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
