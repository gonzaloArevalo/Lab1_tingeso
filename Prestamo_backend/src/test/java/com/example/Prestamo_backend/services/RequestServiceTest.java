package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.time.ZoneId;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


public class RequestServiceTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetRequests_thenReturnsRequestList() {
        // Given:
        Request request1 = new Request();
        request1.setId(1L);
        request1.setRequeststatus("initial review");
        request1.setAmount(30000);
        request1.setTerm(30);
        request1.setRate(4.5f);
        request1.setLoantype("first living");

        Request request2 = new Request();
        request2.setId(2L);
        request2.setRequeststatus("initial review");
        request2.setAmount(30000);
        request2.setTerm(30);
        request2.setRate(4.5f);
        request2.setLoantype("first living");

        ArrayList<Request> expectedRequests = new ArrayList<>(List.of(request1, request2));

        //when
        when(requestRepository.findAll()).thenReturn(expectedRequests);

        //then
        ArrayList<Request> result = requestService.getRequests();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedRequests.size());
        assertThat(result).containsAll(expectedRequests);
    }

    @Test
    void whenGetRequestsByUserId_thenReturnsRequestList() {
        // Given
        Long userId = 1L;
        Request request1 = new Request();
        request1.setId(1L);
        request1.setIduser(userId);
        request1.setRequeststatus("initial review");

        Request request2 = new Request();
        request2.setId(2L);
        request2.setIduser(userId);
        request2.setRequeststatus("initial review");

        List<Request> expectedRequests = List.of(request1, request2);

        when(requestRepository.findByIduser(userId)).thenReturn(expectedRequests);

        // When
        List<Request> result = requestService.getRequestsByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedRequests.size());
        assertThat(result).containsAll(expectedRequests);
    }

    @Test
    void whenGetRequestById_thenReturnsRequest() {
        // Given
        Request request = new Request();
        request.setId(1L);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        Request foundRequest = requestService.getRequestById(1L);

        // Then
        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(1L);
    }

    @Test
    void whenUpdateRequest_thenReturnsUpdatedRequest() {
        // Given
        Request requestToUpdate = new Request();
        requestToUpdate.setId(1L);
        requestToUpdate.setRequeststatus("pending");
        requestToUpdate.setAmount(50000);
        requestToUpdate.setTerm(10);
        requestToUpdate.setRate(4.5f);
        requestToUpdate.setLoantype("second living");

        when(requestRepository.save(requestToUpdate)).thenReturn(requestToUpdate);

        // When
        Request updatedRequest = requestService.updateRequest(requestToUpdate);

        // Then
        assertThat(updatedRequest).isNotNull();
        assertThat(updatedRequest).isEqualTo(requestToUpdate);
    }

    @Test
    void whenDeleteRequestById_thenRequestIsDeleted() throws Exception {
        // Given
        long requestId = 1L;

        // When
        boolean result = requestService.deleteRequestById(requestId);

        // Then
        assertThat(result).isTrue();
        verify(requestRepository, times(1)).deleteById(requestId);
    }

    @Test
    void whenDeleteRequestByIdThrowsException_thenExceptionIsHandled(){
        //Given
        long requestId = 1L;
        doThrow(new RuntimeException("delete error")).when(requestRepository).deleteById(requestId);

        //When / Then
        assertThatThrownBy(() -> requestService.deleteRequestById(requestId))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("delete error");
    }

    @Test
    void testBuildRequestFromParamsWithFullDocumentsSecondLiving() throws Exception {
        // Given
        Long id = 1L;
        Integer amount = 10000;
        Integer term = 12;
        Float rate = 5.0f;
        String loanType = "second living";
        Integer propertyValue = 50000;


        MultipartFile incomeTicket = new MockMultipartFile("incomeTicket", new byte[]{1, 2, 3});
        MultipartFile creditHistorial = new MockMultipartFile("creditHistorial", new byte[]{4, 5, 6});
        MultipartFile appraisalCertificate = new MockMultipartFile("appraisalCertificate", new byte[]{7, 8, 9});
        MultipartFile deedFirstHome = new MockMultipartFile("deedFirstHome", new byte[]{3, 6, 1});

        // when
        Request result = requestService.buildRequestFromParams(
                id, amount, term, rate, loanType, propertyValue, incomeTicket,
                creditHistorial, appraisalCertificate, deedFirstHome, null, null, null, null
        );

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getTerm()).isEqualTo(term);
        assertThat(result.getRate()).isEqualTo(rate);
        assertThat(result.getLoantype()).isEqualTo(loanType);
        assertThat(result.getPropertyvalue()).isEqualTo(propertyValue.intValue());
        assertThat(result.getRequeststatus()).isEqualTo("initial review");
    }

    @Test
    void testBuildRequestFromParamsWithFullDocumentsFirstLiving() throws Exception {
        // Given
        Long id = 1L;
        Integer amount = 10000;
        Integer term = 12;
        Float rate = 5.0f;
        String loanType = "first living";
        Integer propertyValue = 50000;


        MultipartFile incomeTicket = new MockMultipartFile("incomeTicket", new byte[]{1, 2, 3});
        MultipartFile creditHistorial = new MockMultipartFile("creditHistorial", new byte[]{4, 5, 6});
        MultipartFile appraisalCertificate = new MockMultipartFile("appraisalCertificate", new byte[]{7, 8, 9});

        // when
        Request result = requestService.buildRequestFromParams(
                id, amount, term, rate, loanType, propertyValue, incomeTicket,
                creditHistorial, appraisalCertificate, null, null, null, null, null
        );

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getAmount()).isEqualTo(amount);
        assertThat(result.getTerm()).isEqualTo(term);
        assertThat(result.getRate()).isEqualTo(rate);
        assertThat(result.getLoantype()).isEqualTo(loanType);
        assertThat(result.getPropertyvalue()).isEqualTo(propertyValue.intValue());
        assertThat(result.getRequeststatus()).isEqualTo("initial review");
    }

    @Test
    void testBuildRequestFromParamsWithMissingDocuments() throws Exception {
        // Given
        Long id = 2L;
        Integer amount = 20000;
        Integer term = 24;
        Float rate = 6.5f;
        String loanType = "second living";
        Integer propertyValue = 80000;


        MultipartFile incomeTicket = new MockMultipartFile("incomeTicket", new byte[]{1, 2, 3});
        MultipartFile creditHistorial = new MockMultipartFile("creditHistorial", new byte[]{4, 5, 6});
        MultipartFile appraisalCertificate = null; // Documento faltante

        // When
        Request result = requestService.buildRequestFromParams(
                id, amount, term, rate, loanType, propertyValue, incomeTicket,
                creditHistorial, appraisalCertificate, null, null, null, null, null
        );

        // then
        assertThat(result.getRequeststatus()).isEqualTo("pending documentation");
    }

    @Test
    void testBuildRequestFromParamsUnknownLoanType() {
        // Given / when / then
        assertThrows(IllegalArgumentException.class, () -> {
            requestService.buildRequestFromParams(
                    1L, 10000, 12, 5.0f, "unknown loan", 50000, null, null, null, null, null, null, null, null
            );
        });
    }

    @Test
    void whenRequestLoanWithFirstLivingType_thenStatusIsInitialReviewIfAllDocumentsPresent() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile incometicket = new MockMultipartFile("incometicket", "incometicket.pdf", "application/pdf", "income data".getBytes());
        MultipartFile credithistorial = new MockMultipartFile("credithistorial", "credithistorial.pdf", "application/pdf", "credit history data".getBytes());
        MultipartFile appraisalcertificate = new MockMultipartFile("appraisalcertificate", "appraisalcertificate.pdf", "application/pdf", "appraisal data".getBytes());

        // When
        Request newRequest;
        try {
            // When

            newRequest = requestService.requestloan(user.getId(), 100000, 15, 3.5f, "first living", 200000,
                    incometicket, credithistorial, appraisalcertificate, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Se lanzó una excepción al ejecutar requestloan: " + e.getMessage());
            return;
        }

        // Then
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getRequeststatus()).isEqualTo("initial review");
        verify(requestRepository, times(1)).save(newRequest);
    }

    @Test
    void whenRequestLoanWithsecondLivingType_thenStatusIsInitialReviewIfAllDocumentsPresent() throws Exception{
        //Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile incometicket = new MockMultipartFile("incometicket", "incometicket.pdf", "application/pdf", "income data".getBytes());
        MultipartFile appraisalcertificate = new MockMultipartFile("appraisalcertificate", "appraisalcertificate.pdf", "application/pdf", "appraisal data".getBytes());
        MultipartFile credithistorial = new MockMultipartFile("credithistorial", "credithistorial.pdf", "application/pdf", "credit history data".getBytes());
        MultipartFile deedfirsthome = new MockMultipartFile("deedfirsthome", "deedfirsthome.pdf", "application/pdf", "deed first home data".getBytes());

        // When
        Request newRequest = requestService.requestloan(user.getId(), 200000, 25, 4.0f, "second living", 400000,
                incometicket, credithistorial, appraisalcertificate, deedfirsthome, null, null, null, null);

        // Then
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getRequeststatus()).isEqualTo("initial review");
        verify(requestRepository, times(1)).save(newRequest);
    }

    @Test
    void whenRequestLoanWithCommercialPropertiesType_thenStatusIsInitialReviewIfAllDocumentsPresent() throws Exception{
        //Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile incometicket = new MockMultipartFile("incometicket", "incometicket.pdf", "application/pdf", "income data".getBytes());
        MultipartFile appraisalcertificate = new MockMultipartFile("appraisalcertificate", "appraisalcertificate.pdf", "application/pdf", "appraisal data".getBytes());
        MultipartFile buisnessstate = new MockMultipartFile("buisnessstate", "buisnessstate.pdf", "application/pdf", "business state data".getBytes());
        MultipartFile buisnessplan = new MockMultipartFile("buisnessplan", "buisnessplan.pdf", "application/pdf", "business plan data".getBytes());

        // When
        Request newRequest = requestService.requestloan(user.getId(), 250000, 20, 5.0f, "commercial properties", 600000,
                incometicket, null, appraisalcertificate, null, buisnessstate, buisnessplan, null, null);

        // Then
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getRequeststatus()).isEqualTo("initial review");
        verify(requestRepository, times(1)).save(newRequest);
    }

    @Test
    void whenRequestLoanWithRemodelationType_thenStatusIsInitialReviewIfAllDocumentsPresent() throws Exception{
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile incometicket = new MockMultipartFile("incometicket", "incometicket.pdf", "application/pdf", "income data".getBytes());
        MultipartFile rembudget = new MockMultipartFile("rembudget", "rembudget.pdf", "application/pdf", "remodelation budget data".getBytes());
        MultipartFile appcertificatenew = new MockMultipartFile("appcertificatenew", "appcertificatenew.pdf", "application/pdf", "appraisal certificate data".getBytes());

        // When
        Request newRequest = requestService.requestloan(user.getId(), 100000, 15, 4.5f, "remodelation", 300000,
                incometicket, null, null, null, null, null, rembudget, appcertificatenew);

        // Then
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getRequeststatus()).isEqualTo("initial review");
        verify(requestRepository, times(1)).save(newRequest);
    }

    @Test
    void whenRequestLoanWithUnknownType_thenThrowsIllegalArgumentException() {
        // Given: un usuario simulado en la base de datos
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Cuando: se utiliza un tipo de préstamo desconocido
        String unknownLoanType = "exotic investment";

        // When & Then: verifica que la llamada lanza una IllegalArgumentException con el mensaje correcto
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            requestService.requestloan(
                    user.getId(), 10000, 15, 3.5f, unknownLoanType, 50000,
                    null, null, null, null, null, null, null, null
            );
        });

        // Verifica que el mensaje de la excepción es el esperado
        assertThat(exception.getMessage()).contains("Unknown loan type");
    }



    @Test
    void whenRequestLoanWithFirstLivingTypeAndMissingDocuments_thenStatusIsPendingDocumentation() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));


        MultipartFile incometicket = new MockMultipartFile("incometicket", "incometicket.pdf", "application/pdf", "income data".getBytes());
        MultipartFile appraisalcertificate = new MockMultipartFile("appraisalcertificate", "appraisalcertificate.pdf", "application/pdf", "appraisal data".getBytes());

        // When
        Request newRequest = requestService.requestloan(user.getId(), 100000, 15, 3.5f, "first living", 200000,
                incometicket, null, appraisalcertificate, null, null, null, null, null);

        // Then
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.getRequeststatus()).isEqualTo("pending documentation");
        verify(requestRepository, times(1)).save(newRequest);
    }

    @Test
    void whenEvaluateRequest_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-10-1980"));//73  1950
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(5000);
        user.setMovements("200, -100, 300, -50, 150, -75, 100, 200, -150, 300, 100, -200");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023, 01-05-2023, 01-04-2023, 01-03-2023, 01-02-2023, 01-01-2023, 01-12-2022");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(100000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("first living");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("approved");
        assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithSecondLiving_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(5000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(100000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("second living");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("approved");
        assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithCommercialProperties_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(5000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(100000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("commercial properties");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("approved");
        assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithRemodelation_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1970"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(5000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(100000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("remodelation");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithPendingDocumentation_thenStatusRemainsPendingDocumentation() throws ParseException {
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        User user = new User();
        user.setId(1L);
        user.setMovements("200, -100, 300, -50");  // Datos ficticios de movimientos
        user.setMovmntsdate("01-01-2023, 01-02-2023, 01-03-2023, 01-04-2023");  // Fechas de movimientos
        user.setBankaccount(10000);
        user.setIncome(3000);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("pending documentation");
        request.setLoantype("remodelation");
        request.setAmount(50000);
        request.setTerm(10);
        request.setRate(4.5f);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("pending documentation");
    }

    @Test
    void whenEvaluateRequestWithAboveQuota_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(1000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(50000);
        request.setTerm(5);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("remodelation");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithDebtsIncome_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(60000);
        user.setIncome(1000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(50000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("remodelation");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithUnpaid_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(false);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(1000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(50000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("remodelation");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithWork_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-2024"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(1000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(50000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("remodelation");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithMaxFinancial_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-01-1980"));
        user.setTimeinwork(dateFormat.parse("01-01-1980"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(200000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(100000);
        user.setMovements("200, -100, 300, -50, 150, -75");
        user.setMovmntsdate("01-11-2023, 01-10-2023, 01-09-2023, 01-08-2023, 01-07-2023, 01-06-2023");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(500000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(300000);
        request.setLoantype("first living");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("rejected");
        //assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenEvaluateRequestWithAllGood_thenUpdatesRequestStatus() throws ParseException{
        // Given
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        User user = new User();
        user.setId(1L);
        user.setName("Clint");
        user.setAge(dateFormat.parse("01-10-1980"));//73  1950
        user.setTimeinwork(dateFormat.parse("01-01-2010"));
        user.setCreation(dateFormat.parse("01-01-2020"));
        user.setBankaccount(20000);
        user.setCredithistory(true);
        user.setFiles(true);
        user.setDebts(0);
        user.setIncome(1000);
        user.setMovements("80, 100, 60, 75, 90, 120, 130, 85, 95, 110, 70");
        user.setMovmntsdate("30-11-2024,30-10-2024,30-09-2024,30-08-2024,30-07-2024,30-06-2024,30-05-2024,30-04-2024,30-03-2024,29-02-2024,30-01-2024");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Request request = new Request();
        request.setId(1L);
        request.setIduser(user.getId());
        request.setRequeststatus("initial review");
        request.setAmount(1000);
        request.setTerm(20);
        request.setRate(4.5f);
        request.setPropertyvalue(30000);
        request.setLoantype("first living");


        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then
        assertThat(request.getRequeststatus()).isEqualTo("approved");
        assertThat(result).isEqualTo("the request has been approved");
        verify(requestRepository, atLeastOnce()).save(request);
    }

    @Test
    void whenDepositsAreIrregularFrequency_thenReturnsFalse() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setIncome(2000);

        // Movimientos con intervalo de 2 meses, no permitidos
        user.setMovements("150,200,150,200,150,200,150,200,150,200,150,200");
        user.setMovmntsdate("30-11-2024, 30-10-2024, 30-09-2024, 30-08-2024, 30-07-2024, 30-06-2024, 30-05-2024, 30-04-2024, 30-03-2024, 29-02-2024, 30-01-2024");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Request request = new Request();
        request.setIduser(1L);

        // When
        boolean result = requestService.validateperiodicbank(request);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void whenAgeIsNull_thenObtainAgeReturnsZero() {
        // Given
        Date age = null;

        // When
        int result = requestService.obtainAge(age);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void whenMovementResultsInNegativeBalance_thenSaveHistoryReturnsFalse() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setBankaccount(5000);
        user.setMovements("2000,-7000,-3000");
        user.setMovmntsdate("01-11-2023,15-11-2023,01-12-2023");

        Request request = new Request();
        request.setIduser(user.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When
        boolean result = requestService.savehistory(request);

        // Then
        assertThat(result).isFalse();  // Espera que el resultado sea falso debido a un balance negativo
    }

    @Test
    void whenWithdrawalExceedsFiftyPercentOfBalance_thenSaveHistoryReturnsFalse() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setBankaccount(5000);
        user.setMovements("-2000,-3000,3000");  // Este retiro (3000) es más del 50% del balance
        user.setMovmntsdate("01-11-2023,15-11-2023,01-12-2023");  // Fechas en los últimos 12 meses

        Request request = new Request();
        request.setIduser(user.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When
        boolean result = requestService.savehistory(request);

        // Then
        assertThat(result).isFalse();  // Espera que el resultado sea falso debido al retiro mayor al 50%
    }

    @Test
    void whenUserHasLessThanTwoYearsAccount_thenValidateSalOldUsesTwentyPercentThreshold() {
        // Given: Un usuario con menos de 2 años desde la creación de su cuenta
        User user = new User();
        user.setId(1L);
        user.setBankaccount(25000);  // El saldo del banco del usuario
        user.setCreation(Date.from(LocalDate.now().minusMonths(18).atStartOfDay(ZoneId.systemDefault()).toInstant())); // 1.5 años de antigüedad

        Request request = new Request();
        request.setIduser(user.getId());
        request.setAmount(100000); //
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When:
        boolean result = requestService.validatesalold(request);

        // Then:
        assertThat(result).isTrue();
        user.setBankaccount(15000);

        result = requestService.validatesalold(request);
        assertThat(result).isFalse();
    }

    @Test
    void whenRecentWithdrawalExceeds30PercentOfBalance_thenReturnsFalse() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setBankaccount(1000);  // Saldo inicial del usuario

        // Movimientos de dinero dentro de los últimos 6 meses, incluyendo un retiro significativo
        user.setMovements("100,-400,50,-350,100,-200");
        user.setMovmntsdate("01-06-2024,01-07-2024,01-08-2024,01-09-2024,01-10-2024,01-11-2024");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Request request = new Request();
        request.setIduser(1L);

        // When
        boolean result = requestService.recentretire(request);

        // Then
        assertThat(result)
                .as("Validación de retiros recientes significativos en los últimos 6 meses")
                .isFalse();
    }

    @Test
    void whenViewStatus_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("initial review");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("In initial review");
    }

    @Test
    void whenViewStatus2_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("pending documentation");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("missing important documents");
    }

    @Test
    void whenViewStatus3_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("under evaluation");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The application has passed initial review");
    }

    @Test
    void whenViewStatus4_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("pre-approved");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("has been evaluated and meets basic bank criteria");
    }

    @Test
    void whenViewStatus5_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("final approval");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The client has accepted the proposed conditions");
    }

    @Test
    void whenViewStatus6_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("approved");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The application is approved and ready for disbursement");
    }

    @Test
    void whenViewStatus7_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("rejected");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("has been evaluated and does not meet the bank's criteria");
    }

    @Test
    void whenViewStatus8_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("canceled");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The customer canceled the request before approval");
    }

    @Test
    void whenViewStatus9_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("disbursement");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The application is approved, and disbursement is in progress");
    }

    @Test
    void whenViewStatus10_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus(null);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("The request has no defined status");
    }

    @Test
    void whenViewStatus11_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus(null);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(2L);

        // Then
        assertThat(statusMessage).contains("No request found for the given request ID");
    }

    @Test
    void whenViewStatus12_thenReturnsCorrectStatusMessage() {
        // Given
        Request request = new Request();
        request.setRequeststatus("X");
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        // When
        String statusMessage = requestService.viewStatus(1L);

        // Then
        assertThat(statusMessage).contains("Unknown request status");
    }

    @Test
    void whenCalculateTotalCosts_thenReturnsExpectedString() {
        // Given
        Request request = new Request();
        request.setAmount(100000);
        request.setTerm(15); // years
        request.setRate(3.5f);

        // When
        String result = requestService.calculatetotalcosts(request);

        // Then
        assertThat(result).contains("monthly quota:");
    }
}
