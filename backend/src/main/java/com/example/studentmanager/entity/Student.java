package com.example.studentmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_id")
    private Long sId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cl_id")
    private ClassEntity clazz;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;

    @Column(length = 100)
    private String major;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;
}
