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
    private Date age;
    private Date timeinbank;
    private Date timeinwork; //time that the user has been working
    private int bankaccount;
    private Date creation; //date of creation of the account
    private Date retire; //date of retire of money from the account
    private int moneyout;
    private boolean credithistory;
    private int debts; //total debts from the user
    private int income; //income of the user
    private boolean files;
    private String movements; //money movements that the user has made
}
