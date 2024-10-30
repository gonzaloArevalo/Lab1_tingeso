package com.example.Prestamo_backend.services;

import com.example.Prestamo_backend.entitites.Request;
import com.example.Prestamo_backend.entitites.User;
import com.example.Prestamo_backend.repositories.RequestRepository;
import com.example.Prestamo_backend.repositories.UserRepository;
import com.example.Prestamo_backend.services.RequestService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
        // Given: Crea un usuario y una solicitud asociada
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
        //request.setSavingability("solid");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Llama a evaluateRequest
        String result = requestService.RequestEvaluation(request.getId());
        System.out.println("Resultado de Evaluación: " + result);

        // Then: Verifica que el estado de la solicitud y el mensaje de evaluación sean correctos
        assertThat(request.getRequeststatus()).isEqualTo("approved");
        assertThat(result).isEqualTo("the request has been approved");

        // Verifica que el repositorio guarda la solicitud actualizada al menos una vez
        verify(requestRepository, atLeastOnce()).save(request);
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
