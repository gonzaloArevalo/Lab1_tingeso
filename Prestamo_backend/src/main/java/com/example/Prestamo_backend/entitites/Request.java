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
    private Date requestdate;
    private String requeststatus;
    private int amount;
    private int term;
    private float rate;
    private double propertyvalue;
    private boolean credithistory;
    private String loantype;
    private Date dateloan;
    private int quota;

    @Lob
    private byte[] document;
}
