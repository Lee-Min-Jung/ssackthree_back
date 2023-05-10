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
@Table(name = "tb_user_like")
public class UserLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private MenuEntity menuEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
