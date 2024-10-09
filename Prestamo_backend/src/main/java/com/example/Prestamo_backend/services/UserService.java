package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.repositories.UserRepository;
import com.example.Prestamo_backend.entitites.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public ArrayList<User> getUsertype(String type){
        return (ArrayList<User>) userRepository.findByUsertype(type);
    }
}
