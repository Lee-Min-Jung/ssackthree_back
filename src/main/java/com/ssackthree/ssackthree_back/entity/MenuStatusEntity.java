package com.ssackthree.ssackthree_back.entity;

import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
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
@Table(name = "tb_menu_status")
public class MenuStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_status_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private MenuStatusEnum menuStatus;

    @OneToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;
}
