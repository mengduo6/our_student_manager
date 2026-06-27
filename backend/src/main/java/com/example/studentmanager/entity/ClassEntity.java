package com.example.studentmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cl_id")
    private Long clId;

    @Column(nullable = false, unique = true, length = 50)
    private String classname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id")
    private Student monitor;
}
