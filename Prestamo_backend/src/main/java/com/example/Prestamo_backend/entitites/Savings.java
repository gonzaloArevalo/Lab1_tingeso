package com.example.Prestamo_backend.entitites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "savings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Savings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id_savings;

    private int bankaccount;
    private Date creation;
    private Date retire;
    private int moneyout;
}
