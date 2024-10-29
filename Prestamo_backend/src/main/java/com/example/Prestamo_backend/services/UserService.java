package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.repositories.UserRepository;
import com.example.Prestamo_backend.entitites.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public ArrayList<User> getUsertype(String type){
        return (ArrayList<User>) userRepository.findByUsertype(type);
    }

    public ArrayList<User> getUsers(){return (ArrayList<User>) userRepository.findAll();}

    public User saveUser(User user){return userRepository.save(user);}

    public User getUserById(Long id){ return userRepository.findById(id).get();}

    public User getUserByRut(String rut){ return userRepository.findByRut(rut);}

    public User updateUser(User user){ return userRepository.save(user);}

    public boolean deleteUser(Long id) throws Exception{
        try{
            userRepository.deleteById(id);
            return true;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public double getSimlation(int amount, float term, float rate, String loan){
        switch(loan.toLowerCase()){
            case "first living":
                rate = limitrate(3.5f, 5.0f, rate);
                term = limitterm(30, term);
                break;
            case "second living":
                rate = limitrate(4.0f, 6.0f, rate);
                term = limitterm(20, term);
                break;
            case "commercial properties":
                rate = limitrate(5.0f, 7.0f, rate);
                term = limitterm(25, term);
                break;
            case "remodelation":
                rate = limitrate(4.5f, 6.0f, rate);
                term = limitterm(15, term);
                break;
            default:
                throw new IllegalArgumentException("loan not recognized");
        }
        return calculateSimulation(amount, term, rate);
    }

    public float limitrate(float minrate, float maxrate, float rate){
        if(rate < minrate){
            return minrate;
        }
        if (rate > maxrate){
            return maxrate;
        }
        return rate;
    }

    public float limitterm(int maxterm, float term){
        if (term > maxterm){
            return maxterm;
        }
        if(term < 0){
            return 0;
        }
        return term;
    }

    public double calculateSimulation(int amount, float term, float rate){
        float rate0 = (rate/100)/12; //from interanual to intermonthly
        float rate1 = (1+rate0); //(1 + r)
        float term1 = term * 12; //from years to months
        double rate1n = Math.pow(rate1,term1); //(1+r)**n
        double numerator = rate1n * rate0;
        double denominator = rate1n - 1;
        double M = (numerator/denominator)*amount;
        return M;
    }

    public boolean rutverification(String rut){
        return rut != null && !rut.isEmpty();
    }

    public User register(User user){
        if(rutverification(user.getRut()) && user.isFiles()){

            String movements = generateRandomMovements();
            String movementDates = generateMovementDates();

            user.setMovements(movements);
            user.setMovmntsdate(movementDates);

            return userRepository.save(user);
        }
        else{
            throw new IllegalArgumentException("non valid RUT or no documents");
        }
    }

    private String generateRandomMovements() {
        Random random = new Random();
        List<Integer> movementList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            movementList.add(random.nextInt(1001) - 500); // range between -500 y 500
        }
        return String.join(",", movementList.stream().map(String::valueOf).toArray(String[]::new));
    }

    private String generateMovementDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate today = LocalDate.now();
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDate date = today.minusMonths(i);
            dateList.add(date.format(formatter));
        }
        return String.join(",", dateList);
    }
}
