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
@Table(name = "tb_menu_file")
public class MenuFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_file_id")
    private long id;

    private String fileName;
    private String fileOriginName;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;
}
