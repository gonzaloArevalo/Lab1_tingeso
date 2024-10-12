package com.example.Prestamo_backend.entitites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Date;

@Entity
@Table(name = "request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long idrequest;

    private Long iduser;
    private Date requestdate;
    private String requeststatus;
    private int amount;
    private int term;
    private float rate;
    private boolean credithistory;
    private String loantype;
    private Date dateloan;
    private int quota;
    private File document;
}
