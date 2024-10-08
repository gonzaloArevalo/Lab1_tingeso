package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    public Request findByRequeststatus(String requeststatus);

    public Request findByAmount(int amount);
    List<Request> findByAmountGreaterThan(int amount);

    public Request findByTerm(int term);
    List<Request> findByTermGreaterThan(int term);
    List<Request> findByTermLessThan(int term);

    public Request findByRate(float rate);
    List<Request> findByRateGreaterThan(float rate);

    public Request findByCredithistoryIsTrue();
}
