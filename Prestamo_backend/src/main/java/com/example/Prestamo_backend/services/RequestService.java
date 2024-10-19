package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.IOException;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    public ArrayList<Request> getRequests(){
        return (ArrayList<Request>) requestRepository.findAll();
    }

    public Request getRequestById(Long id){return requestRepository.findById(id).get();}

    public Request updateRequest(Request request){return requestRepository.save(request);}

    public boolean deleteRequestById(long id) throws Exception{
        try{
            requestRepository.deleteById(id);
            return true;
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Request requestloan(Long iduser, int amount, int term, float rate, MultipartFile document) throws IOException{
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
        newRequest.setDocument(document.getBytes());

        return requestRepository.save(newRequest);
    }

    public String RequestEvaluation(Long idrequest){
        Request request = requestRepository.findById(idrequest).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setRequeststatus("under evaluation");
        requestRepository.save(request);

        String Savingcapacity;
        int savcapacity = 0;
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

        if(!debtincome(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for relation of income/quota superior to 50%";
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

        if(validatesalary(request)){
            savcapacity++;
        }

        if(savehistory(request)){
            savcapacity++;
        }

        if(validateperiodicbank(request)){
            savcapacity++;
        }

        if(validatesalold(request)){
            savcapacity++;
        }

        if(recentretire(request)){
            savcapacity++;
        }

        if(savcapacity >= 5){
            Savingcapacity = "solid";
            request.setSavingability(Savingcapacity);
        }
        else if (savcapacity ==3 || savcapacity == 4) {
            Savingcapacity = "moderated";
            request.setSavingability(Savingcapacity);
        }
        else if (savcapacity <= 2) {
            Savingcapacity = "insufficent";
            request.setSavingability(Savingcapacity);
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for having to little approved conditions";
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
        if(user.isCredithistory() && user.getDebts() != 0){
            return false;
        }
        else{
            return true;
        }
    }

    private boolean stability(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        int years = calculateyears(user.getTimeinbank());
        return years >=1;
    }

    private boolean debtincome(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double qtamonth = calculatequota(request);
        double totaldebt = user.getDebts();
        double totalmoney = qtamonth + totaldebt;
        double income = user.getIncome();
        double relationqtainc = totalmoney/income;

        return relationqtainc <= 0.50;
    }

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
        int age0 = obtainAge(user.getAge());
        int age = age0 + request.getTerm();
        if(age > 75){
            return false;
        }
        return (75 - age) >= 5;
    }

    private int obtainAge(Date age){
        if(age != null){
            Calendar birth = Calendar.getInstance();
            birth.setTime(age);

            Calendar actualdate = Calendar.getInstance();
            int actualage = actualdate.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            if(actualdate.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)){
                actualage--;
            }

            return actualage;
        }
        else{
            return 0;
        }
    }

    private double calculatequota(Request request){
        int menterm = request.getTerm() * 12;
        float rt = request.getRate();
        double factor = Math.pow(1 + rt, menterm);
        double qtamen = (request.getAmount() * menterm * factor) / (factor - 1);

        return qtamen;
    }

    public boolean validatesalary(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double minsalary = request.getAmount() * 0.10;
        return user.getBankaccount() >= minsalary;
    }

    public boolean savehistory(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Calendar twelvemonths = Calendar.getInstance();
        twelvemonths.add(Calendar.MONTH, -12);
        if(user.getRetire()!=null && user.getRetire().after(twelvemonths.getTime())){
            double perretire = user.getMoneyout() / user.getBankaccount();
            return perretire <= 0.5;
        }
        return true;
    }

    public boolean validateperiodicbank(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double minincome = user.getIncome() * 0.05 * 12;
        return user.getBankaccount() >= minincome;
    }

    public boolean validatesalold(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        int old = calculateyears(user.getCreation());
        double minsalary;
        if(old < 2 && old >= 0){
            minsalary = request.getAmount() * 0.20;
        }
        else{
            minsalary = request.getAmount() * 0.10;
        }
        return user.getBankaccount() >= minsalary;
    }

    public boolean recentretire(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Calendar sixmonths = Calendar.getInstance();
        sixmonths.add(Calendar.MONTH, -6);
        if(user.getRetire() != null && user.getRetire().after(sixmonths.getTime())){
            double perretire = user.getMoneyout() / user.getBankaccount();
            return perretire <= 0.30;
        }
        return true;
    }

    private int calculateyears(Date date){
        Calendar init = Calendar.getInstance();
        init.setTime(date);

        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - init.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < init.get(Calendar.DAY_OF_YEAR)) {
            years--;
        }
        return years;
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

    public String calculatetotalcosts(Request request){
        double qta = calculatequota(request);
        double desgravamen = request.getAmount() * 0.03;
        double fire = 20000;
        double admin = request.getAmount() * 0.01;

        double monthlycost = qta + desgravamen + fire;
        double totalcost = (monthlycost * request.getTerm()) + admin;
        return "monthly quota:" + qta +
                "\nDesgravamen insurance" + desgravamen +
                "\nfire insurance" + fire +
                "\nadministrator fee" + admin +
                "\ntotal monthly costs" + monthlycost +
                "\ntotal costs" + totalcost;
    }
}
