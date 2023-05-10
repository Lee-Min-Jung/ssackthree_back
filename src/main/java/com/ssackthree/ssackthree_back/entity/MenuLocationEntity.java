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
@Table(name = "tb_menu_location")
public class MenuLocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_locaion_id")
    private long id;

    private double latitude;

    private double longitude;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;
}
