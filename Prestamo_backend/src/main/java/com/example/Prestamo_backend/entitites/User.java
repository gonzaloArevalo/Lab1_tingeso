package com.example.Prestamo_backend.entitites;

import lombok.*;

import jakarta.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private String name;
    private String rut;
    private String usertype;
    private int age;
    //private int timeinbank;
    private Date timeinbank;
    private int bankaccount;
    private Date creation;
    private Date retire;
    private int moneyout;
    private boolean credithistory;
    private int debts;
    private int income;
    private boolean files;
}
