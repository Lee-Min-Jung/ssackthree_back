package com.ssackthree.ssackthree_back.entity;

import com.ssackthree.ssackthree_back.enums.BargainStatusEnum;
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
@Table(name = "tb_bargain_order")
public class BargainOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bargain_order_id")
    private long id;

    @Enumerated(EnumType.STRING)
    private BargainStatusEnum status;

    private int bargainPrice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;
}

