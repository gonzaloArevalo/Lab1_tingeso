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
        float rate1 = (1+rate);
        double rate1n = Math.pow(rate1,term);
        double denominator = rate1n * rate;
        double numerator = rate1n - 1;
        double M = (denominator/numerator)*amount;
        return M;
    }

    public boolean rutverification(String rut){
        return rut != null && rut.length() > 0;
    }

    public User register(User user){
        if(rutverification(user.getRut()) && user.isFiles()){
            return userRepository.save(user);
        }
        else{
            throw new IllegalArgumentException("non valid RUT or no documents");
        }
    }
}
