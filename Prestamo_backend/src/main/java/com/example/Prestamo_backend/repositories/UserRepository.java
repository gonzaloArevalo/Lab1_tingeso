package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByRut(String rut);
    List<User> findByUsertype(String usertype);
    List<User> findByAgeGreaterThan(int age);
    List<User> findByFilesTrue();

}
