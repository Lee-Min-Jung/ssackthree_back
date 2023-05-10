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
@Table(name = "tb_store_profile_file")
public class StoreProfileFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_profile_file_id")
    private long id;

    private String fileName;
    private String fileOriginName;
    private String filePath;

    @OneToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;
}
