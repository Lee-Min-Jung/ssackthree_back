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
@Table(name = "tb_town_product_file")
public class MyTownProductFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "town_product_file_id")
    private long id;

    private String fileName;
    private String fileOriginName;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "my_town_product_id")
    private MyTownProductEntity myTownProductEntity;
}

