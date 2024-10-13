package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.io.File;
@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    public Request requestloan(Long iduser, int amount, int term, float rate, File document){
        User user = userRepository.findById(iduser).orElseThrow(()->new IllegalArgumentException("User not found"));

        if(document==null){
            throw new IllegalArgumentException("there is no documents");
        }

        Request newRequest = new Request();
        newRequest.setAmount(amount);
        newRequest.setIduser(iduser);
        newRequest.setTerm(term);
        newRequest.setRate(rate);
        newRequest.setRequeststatus("initial review");
        newRequest.setDateloan(new Date());

        return requestRepository.save(newRequest);
    }

    public String RequestEvaluation(Long idrequest){
        Request request = requestRepository.findById(idrequest).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        //conditions
        if(!quotaincoming(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for been above the 35% of quota realtion";
        }

        if(!unpaiddebts(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for unpaid debts";
        }

        if(!stability(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for no enough time working";
        }



        if(!maxfinancial(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for exceeding loan limit";
        }

        if(!ageofuser(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for having advanced age";
        }

        request.setRequeststatus("approved");
        requestRepository.save(request);
        return "the request has been approved";
    }

    private boolean quotaincoming(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double qta = calculatequota(request);
        double relation =  (qta/user.getIncome())*100;
        if(relation < 35){
            return true;
        }
        else{
            return false;
        }

    }

    private boolean unpaiddebts(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(user.isCredithistory()){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean stability(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Calendar init = Calendar.getInstance();
        init.setTime(user.getTimeinbank());

        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - init.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_YEAR) < init.get(Calendar.DAY_OF_YEAR)){
            years--;
        }
        return years >=1;
    }

    /*private boolean debtincome(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double monthincome = user.getIncome();
        int debts = user.getDebts();
        double qta = calculatequota(request);
    }*/

    private boolean maxfinancial(Request request){
        double maxper = percentage(request.getLoantype());
        double requested = request.getPropertyvalue() * maxper;

        return request.getAmount() <= requested;
    }

    private double percentage(String loantype){
        switch (loantype.toLowerCase()) {
            case "first living":
                return 0.80;
            case "second living":
                return 0.70;
            case "commercial properties":
                return 0.60;
            case "remodelation":
                return 0.50;
            default:
                throw new IllegalArgumentException("loan type not allowed");
        }
    }

    private boolean ageofuser(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        int age = user.getAge() + request.getTerm();
        if(age > 75){
            return false;
        }
        return (75 - age) >= 5;
    }

    private double calculatequota(Request request){
        int menterm = request.getTerm() * 12;
        float rt = request.getRate();
        double factor = Math.pow(1 + rt, menterm);
        double qtamen = (request.getAmount() * menterm * factor) / (factor - 1);

        return qtamen;
    }

    public String viewStatus(Long iduser){
        Request R = requestRepository.findByIduser(iduser);
        if (R == null) {
            return "No request found for the given user ID.";
        }

        if (R.getRequeststatus() == null) {
            return "The request has no defined status.";
        }

        switch(R.getRequeststatus().toLowerCase()){
            case "initial review":
                return "In initial review. The application has been received and is in the process of verification preliminary. At this stage, the system verifies that all the required fields and that the necessary documents have been attached.";
            case "pending documentation":
                return "The application is on hold because one or more are missing important documents or additional customer information is required.";
            case "under evaluation":
                return ("The application has passed the initial review and is being evaluated " +
                        "by an executive. At this stage, the executive makes the different evaluations according to " +
                        "the established rules. For example, documents are analyzed, the relationship is calculated fee/income, " +
                        "credit history is consulted, payment capacity is verified, etc.");
            case "pre-approved":
                return ("The application has been evaluated and meets the basic criteria of the bank, so " +
                        "it has been pre-approved. In this state, the conditions are present Initial credit to the customer.");
            case "final approval":
                return ("The client has accepted the proposed conditions, and the application is in the final approval " +
                        "process. Here are the details finals, contracts are issued, and legal documents are prepared.");
            case "approved":
                return ("The application has been approved and is ready for disbursement. The customer you receive " +
                        "confirmation and the contract is scheduled to be signed.");
            case "rejected":
                return ("The application has been evaluated and, after analysis, does not comply with the requirements " +
                        "of the criteria established by the bank. The customer receives a notification with the reason for the rejection.");
            case "canceled":
                return ("The customer has decided to cancel the request before this is approved. This can happen at any stage of the process.");
            case "disbursment":
                return ("The application has been approved and the process of disbursement of the approved amount. " +
                        "This includes transferring funds to the customer or the seller of the property.");
            default:
                return "Unknown request status.";
        }
    }
}
