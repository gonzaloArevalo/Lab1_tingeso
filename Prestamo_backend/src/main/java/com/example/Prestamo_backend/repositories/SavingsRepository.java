package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long>{

}
