package com.example.Prestamo_backend.controllers;

import com.example.Prestamo_backend.services.RequestService;
import com.example.Prestamo_backend.entitites.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;


import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/request")
@CrossOrigin("*")
public class RequestController {
    RequestService requestService;

    public ResponseEntity<List<Request>> listrequests(){
        List<Request> requests = requestService.getRequests();
        return ResponseEntity.ok(requests);
    }

    //missing getrequestbyid, deleterequestbyid, updaterequest

    @GetMapping("/request")
    public ResponseEntity<Request> requestLoan(@RequestParam Long iduser,
                                               @RequestParam int amount,
                                               @RequestParam int term,
                                               @RequestParam float rate,
                                               @RequestPart File document){
        try{
            //File convertedFile = convertMultipartFileToFile(document);
            Request newRequest = requestService.requestloan(iduser,amount,term,rate,document);
            return ResponseEntity.ok(newRequest);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/evaluate")
    public ResponseEntity<String> evaluateRequest(@RequestParam Long idrequest){
        try{
            String evaluationResult = requestService.RequestEvaluation(idrequest);
            return ResponseEntity.ok(evaluationResult);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> viewStatus(@RequestParam Long iduser){
        try{
            String state = requestService.viewStatus(iduser);
            return ResponseEntity.ok(state);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/totalcosts")
    public ResponseEntity<String> calculateTotalCosts(@RequestBody Request request){
        try{
            String costs = requestService.calculatetotalcosts(request);
            return ResponseEntity.ok(costs);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
