package com.example.Prestamo_backend.controllers;

import com.example.Prestamo_backend.services.RequestService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/request")
@CrossOrigin("*")
public class RequestController {
    RequestService requestService;
}
