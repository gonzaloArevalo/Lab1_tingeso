package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;
@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long>{
    public Savings findByBankaccount(int bankaccount);
    List<Savings> findByBankaccountGreaterThan(int bankaccount);
    List<Savings> findByBankaccountLessThan(int bankaccount);

    List<Savings> findByCreationGreaterThanEqual(Date creation);
    List<Savings> findByCreationLessThan(Date creation);

    List<Savings> findByRetireGreaterThanEqual(Date retire);
    List<Savings> findByRetireLessThan (Date retire);

}
