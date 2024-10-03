package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    public Request findByRequest_status(String request_status);
    List<Request> findByAmountGreaterThan(int amount);
}
