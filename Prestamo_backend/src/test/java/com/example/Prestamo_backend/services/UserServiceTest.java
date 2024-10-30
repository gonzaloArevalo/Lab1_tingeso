package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;


import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    //UserService userService = new UserService();

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
    }

    @Test
    void whenCalculateSimulation_thenCorrect() {
        // Given
        int amount = 100000;
        float term = 15;
        float rate = 5.0f;

        // When
        double simulationResult = userService.calculateSimulation(amount, term, rate);

        // Then
        assertThat(simulationResult).isGreaterThan(0);
    }

    @Test
    void whenRegisterUser_thenMovementsAndDatesAreGenerated() throws ParseException{
        // Given
        User user = new User();
        user.setRut("12.345.678-9");
        user.setName("Clint");
        user.setUsertype("Client");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        user.setAge(dateFormat.parse("01-01-2001"));
        user.setTimeinwork(dateFormat.parse("03-02-2015"));
        user.setBankaccount(100000);
        user.setCreation(dateFormat.parse("04-06-2010"));
        user.setCredithistory(true);
        user.setDebts(0);
        user.setIncome(500);
        user.setFiles(true);

        when(userRepository.save(user)).thenReturn(user);

        // When
        User registeredUser = userService.register(user);

        // Then
        assertThat(registeredUser.getMovements()).isNotEmpty();
        assertThat(registeredUser.getMovmntsdate()).isNotEmpty();
    }

    @Test
    void whenGetUserById_thenReturnsUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Newman");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User foundUser = userService.getUserById(userId);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getName()).isEqualTo("Newman");
    }

    @Test
    void whenUpdateUser_thenUserIsSaved() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Norman");

        // simulate behavior of userRepository
        when(userRepository.save(user)).thenReturn(user);

        // When
        User updatedUser = userService.updateUser(user);

        // Then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(1L);
        assertThat(updatedUser.getName()).isEqualTo("Norman");
    }

    @Test
    void whenDeleteUser_thenReturnsTrue() throws Exception {
        // Given
        Long userId = 1L;

        // When
        boolean isDeleted = userService.deleteUser(userId);

        // Then
        assertThat(isDeleted).isTrue();
        verify(userRepository, times(1)).deleteById(userId); // Verifica que el método deleteById fue llamado una vez
    }

    @Test
    void whenDeleteUserThrowsException_thenExceptionIsHandled() {
        // Given
        Long userId = 1L;
        doThrow(new RuntimeException("Delete error")).when(userRepository).deleteById(userId);

        // When / Then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Delete error");
    }

    @Test
    void whenGetUsers_thenReturnsListOfUsers() {
        // Given
        User user1 = new User();
        user1.setRut("12.345.678-9");
        user1.setName("Rodrigo");

        User user2 = new User();
        user2.setRut("98.765.432-1");
        user2.setName("Felipe");

        ArrayList<User> expectedUsers = new ArrayList<>(List.of(user1, user2));

        // findall
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        ArrayList<User> actualUsers = userService.getUsers();

        // Then
        assertThat(actualUsers).isNotNull();
        assertThat(actualUsers.size()).isEqualTo(2);
        assertThat(actualUsers).containsExactlyElementsOf(expectedUsers);
    }

    @Test
    void whenGetUsertype_thenReturnsListOfUsersWithSpecificType() {
        // Given
        String userType = "Client";

        User user1 = new User();
        user1.setRut("12.345.678-9");
        user1.setName("Gonzalo");
        user1.setUsertype(userType);

        User user2 = new User();
        user2.setRut("98.765.432-1");
        user2.setName("Guillermo");
        user2.setUsertype(userType);

        // List od users
        List<User> expectedUsers = new ArrayList<>(List.of(user1, user2));

        // findByUserType
        when(userRepository.findByUsertype(userType)).thenReturn(expectedUsers);

        // When
        ArrayList<User> actualUsers = userService.getUsertype(userType);

        // Then
        assertThat(actualUsers).isNotNull();
        assertThat(actualUsers.size()).isEqualTo(2);
        assertThat(actualUsers).containsExactlyElementsOf(expectedUsers);
    }

    @Test
    void whenLimitRateIsCalled_thenAppliesCorrectBoundaries() {
        // Test for inferior limit
        assertThat(userService.limitrate(3.5f, 5.0f, 3.0f)).isEqualTo(3.5f);
        // Superior limit
        assertThat(userService.limitrate(3.5f, 5.0f, 5.5f)).isEqualTo(5.0f);
        // In between
        assertThat(userService.limitrate(3.5f, 5.0f, 4.0f)).isEqualTo(4.0f);
    }

    @Test
    void whenLimitTermIsCalled_thenAppliesCorrectBoundaries() {
        // Test for inferior limit
        assertThat(userService.limitterm(30, -5)).isEqualTo(0);
        // Superior limit
        assertThat(userService.limitterm(30, 35)).isEqualTo(30);
        // In between
        assertThat(userService.limitterm(30, 20)).isEqualTo(20);
    }

    @Test
    void whenLoanIsFirstLiving_thenRateAndTermAreLimitedCorrectly() {
        // Given
        int amount = 100000;
        float rate = 6.0f;
        float term = 40;
        String loanType = "first living";

        // When
        double result = userService.getSimlation(amount, term, rate, loanType);
        double rate2 = userService.limitrate(3.5f, 5.0f, 6.0f);
        float term2 = userService.limitterm(30, 40);

        // Then
        assertThat(result).isGreaterThan(0);
        assertThat(rate2).isLessThanOrEqualTo(5.0f); // rate limit check
        assertThat(term2).isLessThanOrEqualTo(30);   // term limit check
    }

    @Test
    void whenLoanIsSecondLiving_thenRateAndTermAreLimitedCorrectly() {
        // Given
        int amount = 80000;
        float rate = 7.0f;
        float term = 25;
        String loanType = "second living";

        // When
        double result = userService.getSimlation(amount, term, rate, loanType);
        double rate2 = userService.limitrate(4.0f, 6.0f, 7.0f);
        float term2 = userService.limitterm(20, 25);

        // Then
        assertThat(result).isGreaterThan(0);
        assertThat(rate2).isLessThanOrEqualTo(6.0f); // rate limit check
        assertThat(term2).isLessThanOrEqualTo(20);   // term limit check
    }

    @Test
    void whenLoanIsCommercialProperties_thenRateAndTermAreLimitedCorrectly() {
        // Given
        int amount = 70000;
        float rate = 8.0f;
        float term = 30;
        String loanType = "commercial properties";

        // When
        double result = userService.getSimlation(amount, term, rate, loanType);
        double rate2 = userService.limitrate(5.0f, 7.0f, 8.0f);
        float term2 = userService.limitterm(25, 30);

        // Then
        assertThat(result).isGreaterThan(0);
        assertThat(rate2).isLessThanOrEqualTo(7.0f); // rate limit check
        assertThat(term2).isLessThanOrEqualTo(25);   // term limit check
    }

    @Test
    void whenLoanIsSRemodelation_thenRateAndTermAreLimitedCorrectly() {
        // Given
        int amount = 90000;
        float rate = 7.0f;
        float term = 20;
        String loanType = "remodelation";

        // When
        double result = userService.getSimlation(amount, term, rate, loanType);
        double rate2 = userService.limitrate(4.5f, 6.0f, 7.0f);
        double term2 = userService.limitterm(15, 20);

        // Then
        assertThat(result).isGreaterThan(0);
        assertThat(rate2).isLessThanOrEqualTo(6.0f); // rate limit check
        assertThat(term2).isLessThanOrEqualTo(15);   // term limit check
    }

    @Test
    void whenLoanTypeNotRecognized_thenThrowsException() {
        // Given
        int amount = 100000;
        float term = 15;
        float rate = 5.0f;
        String invalidLoanType = "unknown type";

        // Then
        assertThatThrownBy(() -> userService.getSimlation(amount, term, rate, invalidLoanType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("loan not recognized");
    }

}
