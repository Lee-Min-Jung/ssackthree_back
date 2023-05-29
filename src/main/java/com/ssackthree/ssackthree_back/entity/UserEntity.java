package com.ssackthree.ssackthree_back.entity;

import com.ssackthree.ssackthree_back.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    private String username;

    private String password;

    private String repName;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @OneToOne(mappedBy = "userEntity")
    private StoreEntity storeEntity;

    @OneToOne(mappedBy = "userEntity")
    private CustomerProfileFileEntity customerProfileFileEntity;

    @OneToMany(mappedBy = "menuEntity")
    private List<UserLikeEntity> userLikeEntityList;

    @OneToMany(mappedBy = "userEntity")
    private List<OrderEntity> orderEntityList;

    @OneToMany(mappedBy = "userEntity")
    private List<UserLocationEntity> userLocationEntityList;

    @OneToMany(mappedBy = "userEntity")
    private List<BargainOrderEntity> bargainOrderEntityList;

    @OneToMany(mappedBy = "userEntity1")
    private List<ChatRoomEntity> chatRoomEntityList1;

    @OneToMany(mappedBy = "userEntity2")
    private List<ChatRoomEntity> chatRoomEntityList2;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessageEntity> sendMessageEntityList;

    @OneToMany(mappedBy = "receiver")
    private List<ChatMessageEntity> receiveMessageEntityList;

}
