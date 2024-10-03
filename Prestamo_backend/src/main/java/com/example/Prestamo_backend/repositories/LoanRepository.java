package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    public Loan findByStatus(String loan_status);
    public Loan findByType(String loan_type);


}
