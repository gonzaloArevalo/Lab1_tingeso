package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    public Request requestloan(Long iduser, int amount, int term, float rate){
        User user = userRepository.findById(iduser).orElseThrow(()->new IllegalArgumentException("User not found"));

        //condicones para ver si puede hacer una solicitud

        Request newRequest = new Request();
        newRequest.setAmount(amount);
        newRequest.setIduser(iduser);
        newRequest.setTerm(term);
        newRequest.setRate(rate);
        newRequest.setRequeststatus("initial review");
        newRequest.setDateloan(new Date());

        return requestRepository.save(newRequest);
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
