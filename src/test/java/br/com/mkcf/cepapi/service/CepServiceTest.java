package br.com.mkcf.cepapi.service;

import br.com.mkcf.cepapi.client.CorreiosClient;
import br.com.mkcf.cepapi.client.WiremockClient;
import br.com.mkcf.cepapi.exception.CepNotFoundException;
import br.com.mkcf.cepapi.messaging.SqsPublisher;
import br.com.mkcf.cepapi.model.dto.CepResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CepServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private WiremockClient correiosClient;

    @Mock
    private CorreiosClient fallbackOperation;

    @Mock
    private SqsPublisher sqsPublisher;

    @InjectMocks
    private CepService cepService;

    @Mock
    private Counter successCounter;

    @Mock
    private Counter failureCounter;
    @Mock
    private Counter fallBackCounter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the counters
        when(meterRegistry.counter("cep.consultas.sucesso")).thenReturn(successCounter);
        when(meterRegistry.counter("cep.service.falha")).thenReturn(failureCounter);
        when(meterRegistry.counter("cep.service.fallback")).thenReturn(fallBackCounter);

    }

    @Test
    void consultarCep_Success() {
        // Arrange
        String cep = "12345678";
        CepResponse mockResponse = new CepResponse();
        mockResponse.setLogradouro("Rua Teste");
        when(correiosClient.consultarCep(cep)).thenReturn(mockResponse);

        // Act
        CepResponse response = cepService.consultarCep(cep);

        // Assert
        assertNotNull(response);
        assertEquals("Rua Teste", response.getLogradouro());
        verify(meterRegistry, times(1)).counter("cep.consultas.sucesso");
        verify(meterRegistry.counter("cep.consultas.sucesso"), times(1)).increment();
        verify(sqsPublisher, times(1)).sendMessage(mockResponse, cep);
    }

    @Test
    void consultarCep_Failure_TriggersFallback() {
        // Arrange
        String cep = "12345678";
        when(correiosClient.consultarCep(cep)).thenThrow(new RuntimeException("Service unavailable"));
        CepResponse fallbackResponse = new CepResponse();
        fallbackResponse.setLogradouro("Rua Fallback");
        when(fallbackOperation.consultarCep(cep)).thenReturn(fallbackResponse);

        // Act
        CepResponse response = cepService.fallbackConsultaCep(cep, new RuntimeException("Service unavailable"));

        // Assert
        assertNotNull(response);
        assertEquals("Rua Fallback", response.getLogradouro());
        verify(meterRegistry, times(1)).counter("cep.service.fallback");
        verify(meterRegistry.counter("cep.service.fallback"), times(1)).increment();
        verify(sqsPublisher, times(1)).sendMessage(fallbackResponse, cep);
    }

    @Test
    void consultarCep_FallbackFails_ThrowsCepNotFoundException() {
        // Arrange
        String cep = "12345678";
        when(correiosClient.consultarCep(cep)).thenThrow(new RuntimeException("Service unavailable"));
        when(fallbackOperation.consultarCep(cep)).thenReturn(null);

        // Act & Assert
        CepNotFoundException exception = assertThrows(CepNotFoundException.class, () ->
                cepService.fallbackConsultaCep(cep, new RuntimeException("Service unavailable"))
        );
        assertEquals("CEP 12345678 não encontrado.", exception.getMessage());
        verify(meterRegistry, times(1)).counter("cep.service.fallback");
        verify(meterRegistry.counter("cep.service.fallback"), times(1)).increment();
        verify(sqsPublisher, never()).sendMessage(any(), any());
    }

    @Test
    void consultarCep_FallbackReturnsEmptyLogradouro_ThrowsCepNotFoundException() {
        // Arrange
        String cep = "12345678";
        CepResponse fallbackResponse = new CepResponse(); // Empty logradouro
        when(fallbackOperation.consultarCep(cep)).thenReturn(fallbackResponse);

        // Act & Assert
        CepNotFoundException exception = assertThrows(CepNotFoundException.class, () ->
                cepService.fallbackConsultaCep(cep, new RuntimeException("Service unavailable"))
        );
        assertEquals("CEP 12345678 não encontrado.", exception.getMessage());
        verify(meterRegistry, times(1)).counter("cep.service.fallback");
        verify(meterRegistry.counter("cep.service.fallback"), times(1)).increment();
        verify(sqsPublisher, never()).sendMessage(any(), any());
    }

    @Test
    void consultarCep_ThrowsException() {
        // Arrange
        String cep = "12345678";
        when(correiosClient.consultarCep(cep)).thenThrow(new RuntimeException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cepService.consultarCep(cep)
        );
        assertEquals("Service unavailable", exception.getMessage());
        verify(meterRegistry, times(1)).counter("cep.service.falha");
        verify(meterRegistry.counter("cep.service.falha"), times(1)).increment();
    }
}