package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.ssackthree.ssackthree_back.entity.UserEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_store")
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private long id;

    private String storeName;

    private String mainAddress;

    private String detailAddress;

    private String zipcode;

    private String phoneNumber;

    private int startTime;

    private int endTime;

    private String holiday;

    private String introduce;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToOne(mappedBy = "storeEntity")
    private StoreProfileFileEntity storeProfileFileEntity;

    @OneToMany(mappedBy = "storeEntity")
    private List<StoreMenuFileEntity> storeMenuFileList = new ArrayList<>();

    @OneToOne(mappedBy = "storeEntity")
    private StoreLocationEntity storeLocationEntity;

    @OneToMany(mappedBy = "storeEntity")
    private List<MenuEntity> menuEntityList = new ArrayList<>();
}
