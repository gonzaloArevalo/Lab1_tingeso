package com.example.Prestamo_backend.entitites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id_user;

    private String name;
    private String rut;
    private String usertype;
    private int age;
    //private int timeinbank;
    private Date timeinbank;
    private boolean files;
    private File document;
}
