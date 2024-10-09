package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
}
