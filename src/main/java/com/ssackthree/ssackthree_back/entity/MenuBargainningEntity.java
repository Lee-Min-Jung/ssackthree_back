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
@Table(name = "tb_menu_bargainning")
public class MenuBargainningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_bargainning_id")
    private long id;

    private int minPrice;
    private int limitTime;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;


}
