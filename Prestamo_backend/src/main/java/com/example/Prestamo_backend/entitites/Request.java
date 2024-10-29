package com.example.Prestamo_backend.entitites;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = false, nullable = false)
    private Long id;

    @Column(name = "iduser")
    private Long iduser;
    private String requeststatus;
    private int amount;
    private int term;
    private float rate;
    private double propertyvalue;
    private String loantype;
    private Date dateloan;
    private double quota;
    private String savingability;

    @Lob
    private byte[] incometicket;
    @Lob
    private byte[] credithistorial;
    @Lob
    private byte[] appraisalcertificate;
    @Lob
    private byte[] deedfirsthome;
    @Lob
    private byte[] buisnessstate;
    @Lob
    private byte[] buisnessplan;
    @Lob
    private byte[] rembudget;
    @Lob
    private byte[] appcertificatenew;
}
