package com.example.Prestamo_backend.controllers;

import com.example.Prestamo_backend.services.RequestService;
import com.example.Prestamo_backend.entitites.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/request")
@CrossOrigin("*")
public class RequestController {
    RequestService requestService;

    @GetMapping("/")
    public ResponseEntity<List<Request>> listrequests(){
        List<Request> requests = requestService.getRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/user/{iduser}")
    public ResponseEntity<List<Request>> getRequestsByUserId(@PathVariable Long iduser) {
        List<Request> userRequests = requestService.getRequestsByUserId(iduser);
        return ResponseEntity.ok(userRequests);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Request> deleteRequestById(@PathVariable Long id) throws Exception{
        var isDeleted = requestService.deleteRequestById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/")
    public ResponseEntity<Request> updateRequest(@RequestBody Request request){
        Request updateRequest = requestService.updateRequest(request);
        return ResponseEntity.ok(updateRequest);
    }

    @PostMapping("/request")
    public ResponseEntity<Request> requestLoan(@RequestParam Long iduser,
                                               @RequestParam int amount,
                                               @RequestParam int term,
                                               @RequestParam float rate,
                                               @RequestPart MultipartFile incometicket,
                                               @RequestPart(required = false) MultipartFile credithistorial,
                                               @RequestPart(required = false) MultipartFile appraisalcertificate,
                                               @RequestPart(required = false) MultipartFile deedfirsthome,
                                               @RequestPart(required = false) MultipartFile buisnessstate,
                                               @RequestPart(required = false) MultipartFile buisnessplan,
                                               @RequestPart(required = false) MultipartFile rembudget,
                                               @RequestPart(required = false) MultipartFile appcertificatenew){
        try{
            //File convertedFile = convertMultipartFileToFile(document);
            Request newRequest = requestService.requestloan(iduser,amount,term,rate,incometicket, credithistorial, appraisalcertificate,
                    deedfirsthome, buisnessstate, buisnessplan,
                    rembudget, appcertificatenew);
            return ResponseEntity.ok(newRequest);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
