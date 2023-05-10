package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Builder
@Table(name = "tb_store_location")
@NoArgsConstructor
@AllArgsConstructor
public class StoreLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_location_id")
    private long id;

    private double latitude;

    private double longitude;

    @OneToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;
}
