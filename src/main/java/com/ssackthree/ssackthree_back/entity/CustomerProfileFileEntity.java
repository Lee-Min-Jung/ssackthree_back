package com.ssackthree.ssackthree_back.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_customer_profile_file")
public class CustomerProfileFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_profile_file_id")
    private long id;

    private String fileName;
    private String fileOriginName;
    private String filePath;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

}
