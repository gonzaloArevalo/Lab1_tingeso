package com.example.Prestamo_backend.repositories;

import com.example.Prestamo_backend.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByRut(String rut);
    List<User> findByUsertype(String usertype);
    List<User> findByAgeGreaterThan(Date age);
    List<User> findByFilesTrue();

    public User findByBankaccount(int bankaccount);
    List<User> findByBankaccountGreaterThan(int bankaccount);
    List<User> findByBankaccountLessThan(int bankaccount);

    List<User> findByCreationGreaterThanEqual(Date creation);
    List<User> findByCreationLessThan(Date creation);

    List<User> findByRetireGreaterThanEqual(Date retire);
    List<User> findByRetireLessThan (Date retire);

}
