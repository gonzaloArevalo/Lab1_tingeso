package com.example.Prestamo_backend.controllers;

import com.example.Prestamo_backend.services.UserService;
import com.example.Prestamo_backend.entitites.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@CrossOrigin("*")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<User>> listUsers(){
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<User>> listUsersclient(){
        List<User> usersclient = userService.getUsertype("client");
        return ResponseEntity.ok(usersclient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/")
    public ResponseEntity<User> saveUser(@RequestBody User user){
        User usernew = userService.saveUser(user);
        return ResponseEntity.ok(usernew);
    }

    @PutMapping("/")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        User userUpdate = userService.updateUser(user);
        return ResponseEntity.ok(userUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable Long id) throws Exception{
        var isDeleted = userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user){
        try {
            User newUser = userService.register(user);
            return ResponseEntity.ok(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/simulation")
    public ResponseEntity<Double> getSimulation(@RequestParam int amount,
                                                @RequestParam float term,
                                                @RequestParam float rate,
                                                @RequestParam String loanType){
        try{
            double simulation = userService.getSimlation(amount, term, rate, loanType);
            return ResponseEntity.ok(simulation);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }
    }


}
