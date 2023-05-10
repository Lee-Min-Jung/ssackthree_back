package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_store_menu_file")
public class StoreMenuFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_menu_file_id")
    private long id;

    private String fileName;
    private String fileOriginName;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;
}
