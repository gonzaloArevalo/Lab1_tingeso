package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.io.IOException;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.time.Period;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    public ArrayList<Request> getRequests(){
        return (ArrayList<Request>) requestRepository.findAll();
    }
    public List<Request> getRequestsByUserId(Long iduser){return requestRepository.findByIduser(iduser);}
    public Request getRequestById(Long id) { return requestRepository.findById(id).orElse(null);}
    public Request updateRequest(Request request){return requestRepository.save(request);}

    public Request buildRequestFromParams(
            Request request, Integer amount, Integer term, Float rate,
            String loanType, Integer propertyValue, MultipartFile incomeTicket,
            MultipartFile creditHistorial, MultipartFile appraisalCertificate,
            MultipartFile deedFirstHome, MultipartFile businessState,
            MultipartFile businessPlan, MultipartFile remBudget, MultipartFile appCertificateNew
    ) {
        request.setAmount(amount);
        request.setTerm(term);
        request.setRate(rate);
        request.setLoantype(loanType);
        request.setPropertyvalue(propertyValue);
        request.setDateloan(new Date());

        boolean fulldocuments;

        switch(loanType.toLowerCase()){
            case "first living":
                fulldocuments = incomeTicket != null && !incomeTicket.isEmpty() &&
                        creditHistorial != null && !creditHistorial.isEmpty() &&
                        appraisalCertificate != null && !appraisalCertificate.isEmpty();
                break;
            case "second living":
                fulldocuments = deedFirstHome != null && !deedFirstHome.isEmpty() &&
                        incomeTicket != null && !incomeTicket.isEmpty() &&
                        appraisalCertificate != null && !appraisalCertificate.isEmpty() &&
                        creditHistorial != null && !creditHistorial.isEmpty();
                break;
            case "commercial properties":
                fulldocuments = businessState != null && !businessState.isEmpty() &&
                        incomeTicket != null && !incomeTicket.isEmpty() &&
                        appraisalCertificate != null && !appraisalCertificate.isEmpty() &&
                        businessPlan != null && !businessPlan.isEmpty();
                break;
            case "remodelation":
                fulldocuments = incomeTicket != null && !incomeTicket.isEmpty() &&
                        remBudget != null && !remBudget.isEmpty() &&
                        appCertificateNew != null && !appCertificateNew.isEmpty();
                break;
            default:
                throw new IllegalArgumentException("Unknown loan type");
        }

        try {
            if (incomeTicket != null) request.setIncometicket(incomeTicket.getBytes());
            if (creditHistorial != null) request.setCredithistorial(creditHistorial.getBytes());
            if (appraisalCertificate != null) request.setAppraisalcertificate(appraisalCertificate.getBytes());
            if (deedFirstHome != null) request.setDeedfirsthome(deedFirstHome.getBytes());
            if (businessState != null) request.setBuisnessstate(businessState.getBytes());
            if (businessPlan != null) request.setBuisnessplan(businessPlan.getBytes());
            if (remBudget != null) request.setRembudget(remBudget.getBytes());
            if (appCertificateNew != null) request.setAppcertificatenew(appCertificateNew.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error");
        }

        if (fulldocuments) {
            request.setRequeststatus("initial review");
        }
        else{
            request.setRequeststatus("pending documentation");
        }

        return request;
    }

    public boolean deleteRequestById(long id) throws Exception{
        try{
            requestRepository.deleteById(id);
            return true;
        }catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Request requestloan(Long iduser, int amount, int term, float rate, String loantype, int propertyvalue,
                               MultipartFile incometicket, MultipartFile credithistorial,
                               MultipartFile appraisalcertificate, MultipartFile deedfirsthome,
                               MultipartFile buisnessstate, MultipartFile buisnessplan,
                               MultipartFile rembudget, MultipartFile appcertificatenew) throws IOException{
        User user = userRepository.findById(iduser).orElseThrow(()->new IllegalArgumentException("User not found"));


        boolean fulldocuments;

        switch(loantype.toLowerCase()){
            case "first living":
                fulldocuments = incometicket != null && !incometicket.isEmpty() &&
                        credithistorial != null && !credithistorial.isEmpty() &&
                        appraisalcertificate != null && !appraisalcertificate.isEmpty();
                break;
            case "second living":
                fulldocuments = deedfirsthome != null && !deedfirsthome.isEmpty() &&
                        incometicket != null && !incometicket.isEmpty() &&
                        appraisalcertificate != null && !appraisalcertificate.isEmpty() &&
                        credithistorial != null && !credithistorial.isEmpty();
                break;
            case "commercial properties":
                fulldocuments = buisnessstate != null && !buisnessstate.isEmpty() &&
                        incometicket != null && !incometicket.isEmpty() &&
                        appraisalcertificate != null && !appraisalcertificate.isEmpty() &&
                        buisnessplan != null && !buisnessplan.isEmpty();
                break;
            case "remodelation":
                fulldocuments = incometicket != null && !incometicket.isEmpty() &&
                        rembudget != null && !rembudget.isEmpty() &&
                        appcertificatenew != null && !appcertificatenew.isEmpty();
                break;
            default:
                throw new IllegalArgumentException("Unknown loan type");
        }

        Request newRequest = new Request();
        newRequest.setAmount(amount);
        newRequest.setIduser(iduser);
        newRequest.setTerm(term);
        newRequest.setRate(rate);
        newRequest.setDateloan(new Date());
        newRequest.setLoantype(loantype);
        newRequest.setPropertyvalue(propertyvalue);
        newRequest.setIncometicket(incometicket != null ? incometicket.getBytes() : null);
        newRequest.setCredithistorial(credithistorial != null ? credithistorial.getBytes() : null);
        newRequest.setAppraisalcertificate(appraisalcertificate != null ? appraisalcertificate.getBytes() : null);
        newRequest.setDeedfirsthome(deedfirsthome != null ? deedfirsthome.getBytes() : null);
        newRequest.setBuisnessstate(buisnessstate != null ? buisnessstate.getBytes() : null);
        newRequest.setBuisnessplan(buisnessplan != null ? buisnessplan.getBytes() : null);
        newRequest.setRembudget(rembudget != null ? rembudget.getBytes() : null);
        newRequest.setAppcertificatenew(appcertificatenew != null ? appcertificatenew.getBytes() : null);

        if (fulldocuments) {
            newRequest.setRequeststatus("initial review");
        }
        else{
            newRequest.setRequeststatus("pending documentation");
        }



        return requestRepository.save(newRequest);
    }

    public String RequestEvaluation(Long idrequest){
        Request request = requestRepository.findById(idrequest).orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if ("pending documentation".equalsIgnoreCase(request.getRequeststatus())) {
            return "Request cannot be evaluated as it is in 'pending documents' status.";
        }

        request.setRequeststatus("under evaluation");
        request.setQuota(calculatequota(request)); //this assign the quota
        requestRepository.save(request);

        String Savingcapacity;
        int savcapacity = 0;
        //conditions
        if(!quotaincoming(request)){
            request.setRequeststatus("rejected");
            requestRepository.save(request);
            return "the request has been rejected for been above the 35% of quota relation";
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

        request.setRequeststatus("pre-approved");
        requestRepository.save(request);

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

    public boolean quotaincoming(Request request){
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
            return true;
        }
        else{
            return false;
        }
    }

    private boolean stability(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        int years = calculateyears(user.getTimeinwork());
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
        if(age > 75 || age0 == 0){
            return false;
        }
        return (75 - age) >= 5;
    }

    public int obtainAge(Date age){
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
        int menterm = request.getTerm() * 12; //from years to months
        float rt = request.getRate()/100/12; //from interanual to monthly
        double factor = Math.pow(1 + rt, menterm);
        double numerator = request.getAmount() * factor * rt;
        double qtamen = (numerator) / (factor - 1);

        return qtamen;
    }

    //--------------------------------------------------------------------R7

    public boolean validatesalary(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        double minsalary = request.getAmount() * 0.10;
        return user.getBankaccount() >= minsalary;
    }

    public boolean savehistory(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now();
        LocalDate twelvemonths = today.minusMonths(12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Integer> mov = Arrays.stream(user.getMovements().split(",")).map(movement -> Integer.parseInt(movement.trim())).collect(Collectors.toList());
        List<LocalDate> dt = Arrays.stream(user.getMovmntsdate().split(",")).map(date -> LocalDate.parse(date.trim(), formatter)).collect(Collectors.toList());

        int balance = user.getBankaccount();

        for(int i = 0; i < mov.size(); i++){
            LocalDate actualdt = dt.get(i);
            int actualmov = mov.get(i);

            if(!actualdt.isBefore(twelvemonths) && !actualdt.isAfter(today)){
                balance = balance + actualmov;

                if(balance <= 0){
                    return false;
                }
                //if there was a withdrawal and that withdraw was superior to the 50% of user account
                else if(actualmov < 0 && Math.abs(actualmov) > 0.5 * balance){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateperiodicbank(Request request){
        User user = userRepository.findById(request.getIduser()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now();
        LocalDate twelvemonths = today.minusMonths(12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Integer> mov = Arrays.stream(user.getMovements().split(",")).map(movement -> Integer.parseInt(movement.trim())).collect(Collectors.toList());
        List<LocalDate> dt = Arrays.stream(user.getMovmntsdate().split(",")).map(date -> LocalDate.parse(date.trim(), formatter)).collect(Collectors.toList());

        float minbank = (float) (0.05 * user.getIncome());

        LocalDate lastAddedDate = null;
        for(int i = 0; i < mov.size(); i++){
            LocalDate actualdt = dt.get(i);
            int actualmov = mov.get(i);
            if(actualmov > 0 && !actualdt.isBefore(twelvemonths) && !actualdt.isAfter(today)){
                if(actualmov < minbank){
                    return false;
                }
                else if(lastAddedDate != null){
                    Period periodDifference = Period.between(lastAddedDate, actualdt).normalized();
                    int monthsDifference = periodDifference.getMonths();
                    if (monthsDifference != 1 && monthsDifference != 3) {
                        System.out.println("Intervalo irregular entre fechas: " + lastAddedDate + " y " + actualdt);
                        return false;
                    }
                }
                lastAddedDate = actualdt;
            }
        }
        return true;
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
        LocalDate today = LocalDate.now();
        LocalDate sixmonths = today.minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Integer> mov = Arrays.stream(user.getMovements().split(",")).map(movement -> Integer.parseInt(movement.trim())).collect(Collectors.toList());
        List<LocalDate> dt = Arrays.stream(user.getMovmntsdate().split(",")).map(date -> LocalDate.parse(date.trim(), formatter)).collect(Collectors.toList());

        int balance = user.getBankaccount();

        for(int i = 0; i< mov.size(); i++){
            LocalDate actualdt = dt.get(i);
            int actualmov = mov.get(i);

            if (!actualdt.isBefore(sixmonths) && !actualdt.isAfter(today)){
                if(actualmov < 0 && Math.abs(actualmov) > 0.3 * balance){
                    return false;
                }

                balance = balance + actualmov;
            }
        }
        return true;
    }

    private int calculateyears(Date date){
        if (date == null) {
            return 0;
        }
        Calendar init = Calendar.getInstance();
        init.setTime(date);

        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - init.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < init.get(Calendar.DAY_OF_YEAR)) {
            years--;
        }
        return years;
    }

    public String viewStatus(Long idRequest){
        Optional<Request> requestOptional = requestRepository.findById(idRequest);
        if (requestOptional.isEmpty()) {
            return "No request found for the given request ID.";
        }
        Request R = requestOptional.get();

        if (R.getRequeststatus() == null) {
            return "The request has no defined status.";
        }

        switch(R.getRequeststatus().toLowerCase()){
            case "initial review":
                return "In initial review. The application has been received and is in the process of verification preliminary. At this stage, the system verifies that all the required fields and that the necessary documents have been attached.";
            case "pending documentation":
                return "The application is on hold because one or more are missing important documents or additional customer information is required.";
            case "under evaluation":
                return "The application has passed initial review and is under evaluation. The executive performs evaluations, including document analysis, fee/income ratio calculation, credit history check, and payment capacity verification.";
            case "pre-approved":
                return "The application has been evaluated and meets basic bank criteria, and is therefore pre-approved. Initial credit conditions are presented to the customer.";
            case "final approval":
                return "The client has accepted the proposed conditions, and the application is in final approval. Final details are reviewed, contracts issued, and legal documents prepared.";
            case "approved":
                return "The application is approved and ready for disbursement. The customer receives confirmation and the contract signing is scheduled.";
            case "rejected":
                return "The application has been evaluated and does not meet the bank's criteria. The customer is notified with the reason for rejection.";
            case "canceled":
                return "The customer canceled the request before approval. This can happen at any stage of the process.";
            case "disbursement":
                return "The application is approved, and disbursement is in progress. Funds are transferred to the customer or the property seller.";
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
        return "monthly quota: " + (int)Math.round(qta) +
                "\nDesgravamen insurance: " + (int)Math.round(desgravamen) +
                "\nfire insurance: " + (int)Math.round(fire) +
                "\nadministrator fee: " + (int)Math.round(admin) +
                "\ntotal monthly costs: " + (int)Math.round(monthlycost) +
                "\ntotal costs: " + (int)Math.round(totalcost);
    }
}
