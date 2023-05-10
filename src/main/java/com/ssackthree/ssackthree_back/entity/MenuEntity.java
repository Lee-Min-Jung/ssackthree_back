package com.ssackthree.ssackthree_back.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssackthree.ssackthree_back.enums.MenuStatusEnum;
import com.ssackthree.ssackthree_back.enums.MenuTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_menu")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private long id;

    private String name;
    private int originalPrice;
    private int discountedPrice;
    private int endTime;

    @Enumerated(EnumType.STRING)
    private MenuTypeEnum type;

    private String isBargainning;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;

    @OneToOne(mappedBy = "menuEntity")
    private MenuLocationEntity menuLocationEntity;

    @OneToMany(mappedBy = "menuEntity")
    private List<MenuFileEntity> menuFileEntity = new ArrayList<>();

    @OneToOne(mappedBy = "menuEntity")
    private MenuStatusEntity menuStatusEntity;

    @OneToOne(mappedBy = "menuEntity")
    private MenuBargainningEntity menuBargainningEntity;

    @OneToMany(mappedBy = "menuEntity")
    private List<UserLikeEntity> userLikeEntityList;

    @OneToOne(mappedBy = "menuEntity")
    private OrderEntity orderEntity;

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
